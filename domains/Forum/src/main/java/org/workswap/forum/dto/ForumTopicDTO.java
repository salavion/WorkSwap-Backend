package org.workswap.forum.dto;

import java.time.LocalDateTime;
import java.util.List;

import org.workswap.user.dto.ShortUserDTO;

public record ForumTopicDTO(
    String openId,
    String title,
    String content,
    String tagName,
    String language,
    LocalDateTime createdAt,
    ShortUserDTO author,
    List<ForumPostRequest> posts,
    int postsCount
) {}
