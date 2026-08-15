package org.workswap.task.datasource.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.workswap.user.datasource.model.User;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor
public class TaskComment {

    public TaskComment(String content,
                       Long authorId,
                       Task task
                       ) {
        this.content = content;
        this.authorId = authorId;
        this.task = task;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 2000)
    private String content;

    private Long authorId;

    @Setter
    @Transient
    private User author;

    @Setter
    @ManyToOne
    private Task task;

    @Setter
    @CreationTimestamp
    private LocalDateTime createdAt;
}
