package org.workswap.order.services;

import org.salavion.security.dto.UserAuthData;
import org.springframework.lang.NonNull;
import org.workswap.order.dto.OrderDTO;

public interface OrderQueryService {
    
    OrderDTO findByChatId(Long chatId, UserAuthData authData);
    OrderDTO findOrderById(@NonNull String orderId, UserAuthData authData);
}
