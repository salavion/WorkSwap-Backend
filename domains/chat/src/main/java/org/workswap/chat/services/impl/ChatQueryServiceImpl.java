package org.workswap.chat.services.impl;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.workswap.chat.dto.ChatDTO;
import org.workswap.chat.dto.ChatDetails;
import org.workswap.chat.dto.ChatMemberDTO;
import org.workswap.chat.dto.ChatsLoadedEvent;
import org.workswap.chat.dto.MessageDTO;
import org.workswap.chat.enums.ChatType;
import org.workswap.chat.services.ChatMappingService;
import org.workswap.chat.services.ChatQueryService;
import org.workswap.listing.datasource.model.Listing;
import org.workswap.listing.datasource.repository.ListingRepository;
import org.workswap.listing.dto.ShortListingDTO;
import org.workswap.listing.services.ListingQueryService;
import org.workswap.sso.security.dto.UserAuthData;
import org.workswap.sso.security.enums.UserStatus;
import org.workswap.user.datasource.model.User;
import org.workswap.user.dto.ShortUserDTO;
import org.workswap.user.services.UserMappingService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;

import org.workswap.chat.datasource.model.Chat;
import org.workswap.chat.datasource.model.ChatParticipant;
import org.workswap.chat.datasource.model.Message;
import org.workswap.chat.datasource.repository.ChatParticipantRepository;
import org.workswap.chat.datasource.repository.ChatRepository;
import org.workswap.chat.datasource.repository.MessageRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Profile({"server"})
public class ChatQueryServiceImpl implements ChatQueryService {

    private static final Logger logger = LoggerFactory.getLogger(ChatQueryService.class);
    
    private final ChatRepository chatRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final MessageRepository messageRepository;
    private final ListingRepository listingRepository;

    private final EntityManager entityManager;
    private final UserMappingService userMappingService;
    private final ChatMappingService mappingService;
    private final ListingQueryService listingQueryService;

    private final ApplicationEventPublisher eventPublisher;
    
    public Chat getOrCreateListingDiscussion(UserAuthData authData, Long listingId) {

        Long sellerId = listingRepository.findAuthorIdByListingId(listingId);

        Optional<Chat> existing = chatRepository.findChatBetweenUsersAndChatTypeAndTargetId(
                sellerId, authData.sub(), ChatType.LISTING_DISCUSSION, listingId);
        if (existing.isPresent()) {
            return existing.get();
        }

        User sellerProxy = entityManager.getReference(User.class, sellerId);
        User clientProxy = entityManager.getReference(User.class, authData.sub());
        Set<User> participants = Set.of(clientProxy, sellerProxy);
        Chat listingDiscussion = new Chat(participants, ChatType.LISTING_DISCUSSION, listingId);

        return chatRepository.save(listingDiscussion);
    }

    public Chat getOrCreatePrivateChat(UserAuthData authData, Long interlocutorId) {
        Optional<Chat> existing = chatRepository.findChatBetweenUsersAndChatTypeAndTargetId(authData.sub(), interlocutorId, ChatType.PRIVATE_CHAT, null);
        if (existing.isPresent()) {
            return existing.get();
        }

        User user1Proxy = entityManager.getReference(User.class, interlocutorId);
        User user2Proxy = entityManager.getReference(User.class, authData.sub());
        Set<User> participants = Set.of(user1Proxy, user2Proxy);
        Chat chat = new Chat(participants, ChatType.PRIVATE_CHAT, null);
        return chatRepository.save(chat);
    }

    public Chat getOrCreateEventChat(UserAuthData authData, Long eventId) {
        if (authData.status().equals(UserStatus.TEMP)) {
            throw new AccessDeniedException("Только для стандартных пользователей");
        }
        Listing event = listingQueryService.getListingById(eventId);
        Optional<Chat> chat = chatRepository.findChatByTargetId(ChatType.EVENT_TOPIC, event.getId());

        User userProxy = entityManager.getReference(User.class, authData.sub());

        if (chat.isPresent()) {
            Chat existing = chat.get();
            
            // TODO Реализовать систему проверки блокировки пользователя в чате перед добавленим его.

            boolean alreadyParticipant = chatParticipantRepository.existsByChatIdAndUserId(existing.getId(), authData.sub());

            if (!alreadyParticipant) {
                existing.getParticipants().add(new ChatParticipant(existing, userProxy));
            }

            return chatRepository.save(existing);
        }

        Chat newChat = new Chat(Set.of(userProxy), ChatType.EVENT_TOPIC, eventId);
        return chatRepository.save(newChat);
    }

    @Transactional
    public List<ChatDTO> getChatsDTOForUser(UserAuthData authData, String locale) {
        List<ChatDTO> chats = chatRepository.findChatsForUser(authData.sub());

        logger.debug("Chats for DTO found: " + chats.size());
        eventPublisher.publishEvent(new ChatsLoadedEvent(authData, chats, locale));
        return chats;
    }

    public List<ChatDetails> getChatDetails(Long userId, List<ChatDTO> chats, String locale) {
        List<Long> chatIds = chats.stream().map(c -> c.id()).toList();
        List<Long> listingIds = chats.stream()
            .filter(c -> Set.of(ChatType.EVENT_TOPIC, ChatType.LISTING_DISCUSSION).contains(c.type()))
            .map(ChatDTO::targetId).toList();

        List<ChatMemberDTO> members = chatParticipantRepository.findMembersByChatIds(chatIds);
        List<ShortListingDTO> listings = listingRepository.findShortListingsByIds(listingIds, userId, locale);

        List<ChatDetails> chatDetails = chats.stream()
            .map(c -> {
                // участники текущего чата
                List<ShortUserDTO> chatMembers = members.stream()
                    .filter(m -> m.chatId().equals(c.id()))
                    .map(m -> new ShortUserDTO(
                        m.id(),
                        m.openId(),
                        m.name(),
                        m.avatarUrl()
                    ))
                    .toList();

                // объявление текущего чата (если тип подходящий)
                ShortListingDTO listing = listings.stream()
                    .filter(l -> l.id().equals(c.targetId()) &&
                                Set.of(ChatType.EVENT_TOPIC, ChatType.LISTING_DISCUSSION).contains(c.type()))
                    .findFirst()
                    .orElse(null);

                return new ChatDetails(c.id(), chatMembers, listing);
            }).toList();

        return Objects.requireNonNull(chatDetails);
    }

    public List<MessageDTO> getMessagesByChatId(Long chatId, UserAuthData authData) {
        logger.debug("Получение сообщений для разговора с ID: {}", chatId);

        if (!chatParticipantRepository.existsByChatIdAndUserId(chatId, authData.sub())) {
            throw new AccessDeniedException("That is not your chat");
        }

        // Получаем все сообщения для этого разговора
        List<Message> messages = messageRepository.findByChatIdOrderBySentAtAsc(chatId);

        // Преобразуем сообщения в DTO и отправляем клиенту
        List<MessageDTO> messageDtos = messages.stream()
            .map(msg -> mappingService.toDTO(msg))
            .collect(Collectors.toList());

        return messageDtos;
    }

    public long getUnreadMessageCount(Long chatId, UserAuthData authData) {
        // Получаем все непрочитанные сообщения для конкретного разговора и пользователя
        return messageRepository.findByChatIdAndSenderIdNotAndReadFalse(chatId, authData.sub()).size();
    }

    public Chat getChatById(Long chatId) {
        if (chatId == null) {
            throw new IllegalStateException("Id чата не найдено");
        }
        return chatRepository.findById(chatId).orElseThrow(
            () -> new EntityNotFoundException("Чата с таким Id не существует"));
    }

    public Boolean isChatTermsAccepted(Long chatId, UserAuthData authData) {
        Boolean accepted = chatParticipantRepository.isChatTermsAccepted(authData.sub(), chatId);
        if (accepted == null) {
            throw new AccessDeniedException("That is not your chat");
        }
        return accepted;
    }

    public List<ShortUserDTO> getChatInterlocutors(Long chatId, UserAuthData authData) {

        ChatType chatType = chatRepository.findTypeById(chatId);

        if (chatType == ChatType.PRIVATE_CHAT || chatType == ChatType.LISTING_DISCUSSION) {
            boolean isParticipant = chatParticipantRepository.existsByChatIdAndUserIdAndChatTypeIn(
                chatId, authData.sub(), List.of(ChatType.PRIVATE_CHAT, ChatType.LISTING_DISCUSSION)
            );
            if (!isParticipant) throw new AccessDeniedException("Нет доступа к приватному чату");
        }

        List<User> interlocutors = chatParticipantRepository.findChatInterlocutorsExcludingUser(chatId, authData.sub());

        return interlocutors.stream().map(user -> userMappingService.toShortDTO(user)).toList();
    }

    public List<MessageDTO> getChatUnreadMessages(UserAuthData authData) {
        List<Message> unreads = messageRepository.findUnreadMessagesByUserId(authData.sub());
        logger.debug("Найдены непрочитанные сообщения для " + authData.ssoUserId() + ": " + unreads.size());
        return unreads.stream().map(m -> mappingService.toDTO(m)).toList();
    }

    public ChatDTO getChatDTO(Long chatId, Long userId) {
        Chat chat = getChatById(chatId);
        return mappingService.convertToDTO(chat, userId);
    }
}
