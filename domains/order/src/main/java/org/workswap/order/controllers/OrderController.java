package org.workswap.order.controllers;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.workswap.order.dto.OrderDTO;
import org.workswap.order.services.OrderCommandService;
import org.workswap.order.services.OrderQueryService;
import org.salavion.security.dto.UserAuthData;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {

    private final OrderQueryService orderQueryService;
    private final OrderCommandService orderCommandService;

    //пометить пермишном
    @GetMapping("/{orderId}")
    public OrderDTO getOrderById(
        @AuthenticationPrincipal UserAuthData authData,
        @PathVariable String orderId
    ) {
        return orderQueryService.findOrderById(orderId, authData);
    }

    //пометить пермишном
    @GetMapping("/{chatId}/chat")
    public OrderDTO getOrderByChat(
        @AuthenticationPrincipal UserAuthData authData,
        @PathVariable Long chatId
    ) {
        return orderQueryService.findByChatId(chatId, authData);
    }

    //пометить пермишном
    @GetMapping
    public OrderDTO getOrCreateOrder(
        @AuthenticationPrincipal UserAuthData authData,
        @RequestParam Long listingId
    ) {
        return orderCommandService.getOrderDTO(authData, listingId);
    }
}
