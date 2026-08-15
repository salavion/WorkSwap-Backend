package org.workswap.order.services;

import org.workswap.order.datasource.model.Order;
import org.workswap.order.dto.OrderDTO;

public interface OrderMappingService {
    
    OrderDTO toDTO(Order order);
}
