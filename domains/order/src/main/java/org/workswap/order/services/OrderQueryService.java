package org.workswap.order.services;

import org.salavion.security.dto.UserAuthData;
import org.workswap.order.dto.OrderDTO;

public interface OrderQueryService {
    
    OrderDTO findByChatId(Long chatId, UserAuthData authData);
    OrderDTO findOrderById(String orderId, UserAuthData authData);
}
