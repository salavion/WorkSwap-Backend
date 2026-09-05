package org.workswap.chat.services.impl;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.workswap.chat.dto.ChatDTO;
import org.workswap.chat.dto.MessageDTO;
import org.workswap.chat.enums.ChatStatus;
import org.workswap.chat.services.ChatCommandService;
import org.workswap.chat.services.ChatMappingService;
import org.workswap.chat.services.ChatQueryService;
import org.workswap.sso.security.dto.UserAuthData;
import org.workswap.user.datasource.model.User;
import org.workswap.user.datasource.repository.UserRepository;
import org.workswap.chat.datasource.model.Chat;
import org.workswap.chat.datasource.model.Message;
import org.workswap.chat.datasource.repository.ChatParticipantRepository;
import org.workswap.chat.datasource.repository.ChatRepository;
import org.workswap.chat.datasource.repository.MessageRepository;
import org.workswap.chat.datasource.view.ChatParticipantView;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Profile({"server"})
public class ChatCommandServiceImpl implements ChatCommandService {

    private static final Logger logger = LoggerFactory.getLogger(ChatCommandService.class);

    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatParticipantRepository chatParticipantRepository;
    private final UserRepository userRepository;

    private final ChatQueryService chatQueryService;
    private final ChatMappingService mappingService;
    /* private final NotificationCommandService notificationCommandService; */

    public void notifyChatUpdate(ChatDTO chatDto, String recipientOpenId) {

        // Отправляем обновление конкретному пользователю
        messagingTemplate.convertAndSendToUser(
            recipientOpenId,
            "/queue/chat.chats-updates",
            chatDto
        );
    }

    @Transactional
    public void sendMessage(MessageDTO dto, UserAuthData authData) {

        Long chatId = dto.chatId();

        Optional<Chat> optChat = chatRepository.fingByChatIdAndUserId(chatId, authData.sub());

        // 1. Проверка доступа (1 быстрый EXISTS)
        if (optChat.isEmpty()) {
            throw new AccessDeniedException("That is not your chat");
        }

        User sender = userRepository.findBySub(authData.sub()).orElseThrow();

        // 2. Создание сообщения (без Chat entity)
        Message message = messageRepository.save(new Message(
            optChat.get(),
            sender,
            dto.text()
        ));

        MessageDTO msgDto = Objects.requireNonNull(
            mappingService.toDTO(message)
        );

        // 3. Помечаем чат постоянным (UPDATE)
        chatRepository.setStatus(chatId, ChatStatus.ACTIVE);

        // 4. Готовим DTO чата ОДИН РАЗ
        ChatDTO chatDto = Objects.requireNonNull(
            chatQueryService.getChatDTO(chatId, authData.sub())
        );

        // 5. Участники — 1 запрос
        List<ChatParticipantView> participants =
            chatParticipantRepository.findParticipantsView(chatId);

        for (ChatParticipantView p : participants) {

            if (p.getSub().equals(authData.sub())) {
                continue;
            }

            messagingTemplate.convertAndSendToUser(
                p.getSub(),
                "/queue/chat/messages",
                msgDto
            );

            notifyChatUpdate(chatDto, p.getSub());
        }
    }

    @Transactional
    public void markMessagesAsRead(Long chatId, UserAuthData authData) {
        List<Message> messages = messageRepository.findByChatIdAndSenderSubNotAndReadFalse(chatId, authData.sub());
        for (Message m : messages) {
            m.setRead(true);
        }
        messageRepository.markMessagesAsRead(chatId, authData.sub());

        ChatDTO chatDto = Objects.requireNonNull(
            chatQueryService.getChatDTO(chatId, authData.sub()));
        notifyChatUpdate(chatDto, authData.sub());

        List<MessageDTO> dtos = Objects.requireNonNull(
            messages.stream().map(m -> mappingService.toDTO(m)).toList());

        messagingTemplate.convertAndSendToUser(authData.sub(), "/queue/chat/messages", dtos);
    }

    public void setPermanentChat(Chat chat) {
        chat.setStatus(ChatStatus.ACTIVE);
        chatRepository.save(chat);
    }

    @Transactional
    public void deleteChat(Chat chat) {
        
        logger.debug("Начинаем удаление чата {}", chat.getId());
        List<Message> messages = messageRepository.findByChatIdOrderBySentAtAsc(chat.getId());

        logger.debug("> Чистим сообщения");

        if (!messages.isEmpty()) {
            for(Message msg : messages) {
                logger.debug(">> Удаляем сообщение {}", msg.getId());
                messageRepository.delete(msg);
            }
        } else {
            logger.debug("> В чате не было сообщений");
        }

        logger.debug("Удаляем чат");
        
        chatRepository.delete(chat);
    }

    public void acceptChatTerms(Long chatId, UserAuthData authData) {
        int updated = chatParticipantRepository.acceptChatTerms(
            chatId,
            authData.sub()
        );

        if (updated == 0) {
            throw new AccessDeniedException("That is not your chat");
        }
    }

    public void deleteTemporaryChats(UserAuthData authData) {
        logger.debug("Запрос на удаление временных диалогов от пользователя: {}", authData.sub());

        chatRepository.deleteEmptyChatsByStatus(authData.sub(), ChatStatus.TEMPORARY);
    }

    public void deleteUserFromChats(Long userId) {
        chatParticipantRepository.deleteAllByUserId(userId);
    }
}
