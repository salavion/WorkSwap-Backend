package org.workswap.forum.dto;

import java.time.LocalDateTime;
import java.util.List;

import org.workswap.user.dto.ShortUserDTO;

public record ForumPostRequest(
    String openId,
    String content,
    LocalDateTime createdAt,
    ShortUserDTO author,
    List<ForumCommentDTO> comments
) {}