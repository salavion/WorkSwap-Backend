package org.workswap.task.dto;

import java.time.LocalDateTime;

import org.workswap.user.dto.ShortUserDTO;

public record TaskCommentDTO(
    Long id,
    Long taskId,
    String content,
    LocalDateTime createdAt,
    ShortUserDTO author
) {}
