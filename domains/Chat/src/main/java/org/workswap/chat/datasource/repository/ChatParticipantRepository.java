package org.workswap.chat.datasource.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.workswap.chat.datasource.model.Chat;
import org.workswap.chat.datasource.model.ChatParticipant;
import org.workswap.chat.datasource.model.ChatParticipantId;
import org.workswap.chat.datasource.view.ChatParticipantView;
import org.workswap.chat.dto.ChatMemberDTO;
import org.workswap.chat.enums.ChatType;
import org.workswap.user.datasource.model.User;

@Repository
public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, ChatParticipantId> {

    // Найти запись по пользователю и разговору
    ChatParticipant findByUserAndChat(User user, Chat chat);

    boolean existsByChatIdAndUserId(Long chatId, Long userId);

    @Query("SELECT cp.chatTermsAccepted FROM ChatParticipant cp WHERE cp.user.id = :userId AND cp.chat.id = :chatId")
    Boolean isChatTermsAccepted(@Param("userId") Long userId, @Param("chatId") Long chatId);

    @Query("""
        SELECT CASE WHEN COUNT(cp) > 0 THEN true ELSE false END
        FROM ChatParticipant cp
        WHERE cp.chat.id = :chatId
        AND cp.user.id = :userId
        AND cp.chat.chatType IN :types
    """)
    boolean existsByChatIdAndUserIdAndChatTypeIn(
            @Param("chatId") Long chatId,
            @Param("userId") Long userId,
            @Param("types") List<ChatType> types
    );

    // Найти все записи по пользователю
    List<ChatParticipant> findAllByUser(User user);

    @Query("SELECT cp.chat FROM ChatParticipant cp WHERE cp.user = :user")
    List<Chat> findAllChatsByUser(@Param("user") User user);

    @Query("""
        SELECT cp.chat
        FROM ChatParticipant cp
        WHERE cp.user = :user
        AND cp.chat.chatType = :chatType
    """)
    List<Chat> findAllChatsByUserAndType(@Param("user") User user, @Param("chatType") ChatType chatType);

    @Query("""
        SELECT cp.chat
        FROM ChatParticipant cp
        WHERE cp.user = :user
        AND cp.chat.chatType = :chatType
        AND cp.chat.targetId = :targetId
    """)
    List<Chat> findAllByUserAndTarget(
        @Param("user") User user,
        @Param("chatType") ChatType chatType,
        @Param("targetId") Long targetId);

    boolean existsByChatAndUser(Chat chat, User user);

    @Query("""
        SELECT cp
        FROM ChatParticipant cp
        WHERE cp.chat.chatType = :chatType
        AND cp.chat.targetId = :targetId
    """)
    List<ChatParticipant> findAllByTarget(
            @Param("chatType") ChatType chatType,
            @Param("targetId") Long targetId);

    @Modifying
    @Transactional
    @Query("""
        UPDATE ChatParticipant cp
        SET cp.chatTermsAccepted = true
        WHERE cp.chat.id = :chatId
        AND cp.user.id = :userId
    """)
    int acceptChatTerms(
        @Param("chatId") Long chatId,
        @Param("userId") Long userId
    );

    @Query("""
        select 
            cp.user.id as userId,
            cp.user.openId as openId
        from ChatParticipant cp
        where cp.chat.id = :chatId
    """)
    List<ChatParticipantView> findParticipantsView(@Param("chatId") Long chatId);

    @Query("""
        SELECT u
        FROM ChatParticipant cp
        JOIN User u ON cp.user.id = u.id
        WHERE cp.chat.id = :chatId
        AND u.id <> :userId
    """)
    List<User> findChatInterlocutorsExcludingUser(
            @Param("chatId") Long chatId,
            @Param("userId") Long userId
    );

    @Query("""
        SELECT new org.workswap.chat.dto.ChatMemberDTO(
            cp.chat.id,
            u.id,
            u.openId,
            u.name,
            u.avatarUrl
        )
        FROM ChatParticipant cp
        JOIN cp.user u
        WHERE cp.chat.id IN :chatIds
    """)
    List<ChatMemberDTO> findMembersByChatIds(@Param("chatIds") List<Long> chatIds);
}