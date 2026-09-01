package org.workswap.chat.dto;

import org.workswap.security.dto.UserAuthData;
import java.util.List;

public record ChatsLoadedEvent(
    UserAuthData authData,
    List<ChatDTO> chats,
    String locale
) {}
