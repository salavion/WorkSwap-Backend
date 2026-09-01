package org.workswap.order.services.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.workswap.order.datasource.model.Order;
import org.workswap.order.datasource.repository.OrderRepository;
import org.workswap.order.dto.OrderDTO;
import org.workswap.order.services.OrderMappingService;
import org.workswap.order.services.OrderQueryService;
import org.workswap.security.dto.UserAuthData;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@Profile("server")
@RequiredArgsConstructor
public class OrderQueryServiceImpl implements OrderQueryService{

    private static final Logger logger = LoggerFactory.getLogger(OrderQueryService.class);

    private final OrderRepository orderRepository;
    private final OrderMappingService orderMappingService;

    public OrderDTO findByChatId(Long chatId, UserAuthData authData) {
        Order order = orderRepository.findByChatId(chatId);
        logger.debug("Заказ: {}", order.getId());

        if (!orderRepository.existsByIdAndUserIsBuyerOrSeller(order.getId(), authData.id())) {
            throw new AccessDeniedException("Вы не являетесь участником сделки");
        }

        return orderMappingService.toDTO(order);
    }

    public OrderDTO findOrderById(String orderId, UserAuthData authData) {

        if (!orderRepository.existsByIdAndUserIsBuyerOrSeller(orderId, authData.id())) {
            throw new AccessDeniedException("Вы не являетесь участником сделки");
        }

        Order order = orderRepository.findById(orderId).orElseThrow(
            () -> new EntityNotFoundException("Заказ не найден"));

        return orderMappingService.toDTO(order);
    }
}
