package org.workswap.forum.dto;

import java.time.LocalDateTime;

import org.workswap.user.dto.ShortUserDTO;

public record ForumPostDTO(
    String topicOpenId,
    String openId,
    String content,
    LocalDateTime createdAt,
    ShortUserDTO author
) {}
