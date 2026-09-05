package org.workswap.chat.services;

import org.workswap.chat.datasource.model.Chat;
import org.workswap.chat.dto.ChatDTO;
import org.workswap.chat.dto.MessageDTO;
import org.workswap.chat.datasource.model.Message;

public interface ChatMappingService {

    ChatDTO convertToDTO(Chat chat, String userSub);
    
    MessageDTO toDTO(Message message);
}
