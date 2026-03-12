package org.workswap.chat.dto;

public record ChatMemberDTO(
    Long chatId,
    Long id,
    String openId,
    String name,
    String avatarUrl
) {}