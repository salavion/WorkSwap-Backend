package org.workswap.chat.datasource.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

@Getter
@Entity
@NoArgsConstructor
public class Message {

    public Message(Long chatId,
                   Long senderId,
                   String text) {
        this.chatId = chatId;
        this.senderId = senderId;
        this.text = text;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "chat_id", nullable = false)
    private Long chatId;

    @Column(name = "sender_id", nullable = false)
    private Long senderId;

    @Column(columnDefinition = "TEXT")
    private String text;

    @CreationTimestamp
    private LocalDateTime sentAt;

    @Setter
    @Column(name = "is_read")
    private boolean read = false;

    public static Message create(Long chatId, Long senderId, String text) {
        Message m = new Message();
        m.chatId = chatId;
        m.senderId = senderId;
        m.text = text;
        return m;
    }
}
