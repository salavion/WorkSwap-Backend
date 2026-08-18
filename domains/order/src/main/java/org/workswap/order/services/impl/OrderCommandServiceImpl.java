package org.workswap.order.services.impl;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.workswap.listing.datasource.model.Listing;
import org.workswap.listing.datasource.repository.ListingRepository;
import org.workswap.listing.services.SecurityFilterService;
import org.workswap.order.datasource.model.Order;
import org.workswap.order.datasource.repository.OrderRepository;
import org.workswap.order.dto.OrderDTO;
import org.workswap.order.enums.OrderStatus;
import org.workswap.order.services.OrderCommandService;
import org.workswap.order.services.OrderMappingService;
import org.workswap.shared.enums.Importance;
import org.workswap.shared.events.notification.CreateNotificationCommand;
import org.workswap.user.datasource.model.User;
import org.salavion.security.dto.UserAuthData;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Service
@Profile("production")
@RequiredArgsConstructor
public class OrderCommandServiceImpl implements OrderCommandService {
    
    private final OrderRepository orderRepository;

    private final OrderMappingService orderMappingService;
    private final ListingRepository listingRepository;
    private final SecurityFilterService securityFilterService;
    private final EntityManager entityManager;
    private final ApplicationEventPublisher eventPublisher;

    public OrderDTO getOrderDTO(UserAuthData authData, Long listingId) {

        if (listingId == null) {
            throw new IllegalStateException("Заказа без объявления быть не может!");
        }

        if (!securityFilterService.listingAuthorFilter(authData, listingId)) {
            throw new AccessDeniedException("Это ваше объявление");
        }

        Listing listing = listingRepository.findById(listingId).orElse(null);

        User author = listing.getAuthor();

        User userProxy = entityManager.getReference(User.class, authData.id());

        Order order = getOrCreateOrder(userProxy, author, listing);

        OrderDTO orderDto = orderMappingService.toDTO(order);

        return orderDto;
    }

    public Order getOrCreateOrder(User buyer, User seller, Listing listing) {

        Order order = orderRepository.findByBuyerAndSellerAndListing(buyer, seller, listing);

        boolean changed = false;

        if (order == null) {
            order = new Order(listing, buyer, seller);
            changed = true;
        }

        return changed ? orderRepository.save(order) : order;
    }

    public void markConfirmed(String orderId, Long userId) {
        if (orderId == null) {
            throw new IllegalStateException("ID заказа не указан");
        }

        Order order = orderRepository.findById(orderId).orElse(null);

        if (order.getStatus() != OrderStatus.CREATED) {
            throw new IllegalStateException("Заказ уже завершён или отменён");
        }

        boolean isBuyer = order.getBuyer().getId().equals(userId);
        boolean isSeller = order.getSeller().getId().equals(userId);

        if (!isBuyer && !isSeller) {
            throw new AccessDeniedException("Вы не являетесь участником сделки");
        }

        if (isBuyer && order.isConfirmedByBuyer() || isSeller && order.isConfirmedBySeller()) {
            throw new IllegalStateException("Вы уже подтвердили выполнение");
        }

        if (isBuyer) order.setConfirmedByBuyer(true);
        if (isSeller) order.setConfirmedBySeller(true);

        if (order.isConfirmedByBuyer() && order.isConfirmedBySeller()) {
            order.setStatus(OrderStatus.COMPLETED);

            eventPublisher.publishEvent(new CreateNotificationCommand(
                "Заказ завершён", 
                "Продавец и клиент подтвердили выполнение заказа, \"%s\" теперь считается завершённым".formatted(order.getId()),
                "https://workswap.org/order/" + order.getId(),
                order.getBuyer().getId(),
                Importance.INFO
            ));

            eventPublisher.publishEvent(new CreateNotificationCommand(
                "Заказ завершён", 
                "Продавец и клиент подтвердили выполнение заказа, \"%s\" теперь считается завершённым".formatted(order.getId()),
                "https://workswap.org/order/" + order.getId(),
                order.getSeller().getId(),
                Importance.INFO
            ));
        }

        orderRepository.save(order);
    }
}
