package org.workswap.chat.services;

import org.workswap.chat.datasource.model.Chat;
import org.workswap.chat.dto.ChatDTO;

public interface ChatMappingService {

    ChatDTO convertToDTO(Chat chat, String userSub);
}
