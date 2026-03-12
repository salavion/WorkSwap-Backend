package org.workswap.chat.datasource.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.workswap.chat.datasource.model.Message;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    // Получить все непрочитанные сообщения для пользователя
    List<Message> findBySenderIdNotAndReadFalse(Long senderId);

    // Получить все непрочитанные сообщения для пользователя в конкретном разговоре
    List<Message> findByChatIdAndSenderIdNotAndReadFalse(Long chatId, Long senderId);

    List<Message> findByChatIdOrderBySentAtAsc(Long chatId);

    long countByChatIdAndSenderIdNotAndReadFalse(Long chatId, Long senderId);

    // Новый метод: получить сообщения по ID разговора (с сортировкой по времени)
    Page<Message> findByChatIdOrderBySentAtDesc(Long chatId, Pageable pageable);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        UPDATE Message m
        SET m.read = true
        WHERE m.chatId = :chatId
        AND m.senderId <> :userId
        AND m.read = false
    """)
    void markMessagesAsRead(
            @Param("chatId") Long chatId,
            @Param("userId") Long userId
    );

    @Query("""
        SELECT m
        FROM Message m
        WHERE m.chatId IN (
            SELECT cp.chat.id
            FROM ChatParticipant cp
            WHERE cp.user.id = :userId
        )
        AND m.read = false
        AND m.senderId <> :userId
    """)
    List<Message> findUnreadMessagesByUserId(@Param("userId") Long userId);

    Optional<Message> findTopByChatIdOrderByIdDesc(Long chatId);
}


