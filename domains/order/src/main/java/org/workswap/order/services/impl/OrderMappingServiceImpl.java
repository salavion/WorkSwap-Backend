package org.workswap.order.services.impl;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.workswap.chat.datasource.model.Chat;
import org.workswap.listing.datasource.model.Listing;
import org.workswap.order.datasource.model.Order;
import org.workswap.order.dto.OrderDTO;
import org.workswap.order.services.OrderMappingService;
import org.workswap.user.datasource.model.User;

import lombok.RequiredArgsConstructor;

@Service
@Profile("production")
@RequiredArgsConstructor
public class OrderMappingServiceImpl implements OrderMappingService{
    
    public OrderDTO toDTO(Order order) {

        Listing listing = order.getListing();
        User buyer = order.getBuyer();
        User seller = order.getSeller();
        Chat chat =  order.getChat();

        return new OrderDTO(
            order.getId(),
            listing != null ? listing.getId() : null,
            buyer != null ? buyer.getId() : null,
            seller != null ? seller.getId() : null,
            chat != null ? chat.getId() : null,
            order.getStatus().toString(),
            order.isConfirmedByBuyer(),
            order.isConfirmedBySeller(),
            order.getCreatedAt()
        );
    }
}
