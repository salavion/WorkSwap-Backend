package org.workswap.forum.dto;

import java.time.LocalDateTime;

import org.workswap.user.dto.ShortUserDTO;

public record ForumCommentDTO(
    Long id, 
    String content, 
    String postOpenId, 
    LocalDateTime createdAt,
    ShortUserDTO author
) {
}
