package org.workswap.chat.services.impl;

import java.time.LocalDateTime;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.workswap.chat.dto.ChatDTO;
import org.workswap.chat.dto.MessageDTO;
import org.workswap.chat.enums.ChatStatus;
import org.workswap.chat.enums.ChatType;
import org.workswap.chat.services.ChatMappingService;
import org.workswap.chat.datasource.model.Chat;
import org.workswap.chat.datasource.model.Message;
import org.workswap.chat.datasource.repository.MessageRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Profile({"server", "statistic"})
public class ChatMappingServiceImpl implements ChatMappingService {
 
    private static final Logger logger = LoggerFactory.getLogger(ChatMappingService.class);

    private final MessageRepository messageRepository;

    public ChatDTO convertToDTO(Chat chat, Long userId) {
        logger.debug("Конвертация в дто начата разговора: " + chat.getId());

        ChatStatus status = chat.getStatus();
        ChatType type = chat.getChatType();

        logger.debug("Определяем, есть ли новые сообщения");

        long unreadcount = messageRepository.countByChatIdAndSenderIdNotAndReadFalse(chat.getId(), userId);

        logger.debug("Обработка последнего сообщения");
        // Обработка последнего сообщения
        Optional<Message> lastMessage = messageRepository.findTopByChatIdOrderByIdDesc(chat.getId());
        String lastMessagePreview = null;
        LocalDateTime lastMessageTime = null;

        if (lastMessage.isPresent()) {
            Message existing = lastMessage.get();
            lastMessagePreview = existing.getText();
            lastMessageTime = existing.getSentAt();
        } else {
            lastMessageTime = chat.getCreatedAt();
        }

        ChatDTO dto = new ChatDTO(
            chat.getId(),
            unreadcount,
            lastMessagePreview,
            lastMessageTime,
            status,
            type,
            chat.getTargetId()
        );

        logger.debug("Конвертация закончена");

        return dto;
    }

    // Кастомные параметры которые сделаны для того чтобы можно было указать 
    // их сразу если они имеются в методе, и тем самым ускорить загрузку
    public MessageDTO toDTO(Message message) {
        return new MessageDTO(
            message.getId(),
            message.getText(),
            message.getSentAt(),
            message.getSenderId(),
            message.getChatId(),
            message.isRead()
        );
    }
}
