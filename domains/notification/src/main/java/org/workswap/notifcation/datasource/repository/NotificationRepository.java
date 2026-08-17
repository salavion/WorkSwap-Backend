package org.workswap.notifcation.datasource.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.workswap.notifcation.datasource.model.Notification;
import org.workswap.user.datasource.model.User;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByRecipient(User recipient);

    List<Notification> findByRecipientId(Long recipientId);

    @Modifying
    @Transactional
    @Query("""
        UPDATE Notification n
        SET n.isRead = true
        WHERE n.id = :notificationId
        AND n.recipient.id = :userId
        AND n.isRead = false
    """)
    int markAsRead(
        @Param("notificationId") Long notificationId,
        @Param("userId") Long userId
    );
}
