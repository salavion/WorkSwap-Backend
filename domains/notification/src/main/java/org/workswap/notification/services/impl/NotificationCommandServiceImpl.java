package org.workswap.notification.services.impl;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.workswap.notification.datasource.model.Notification;
import org.workswap.notification.datasource.repository.NotificationRepository;
import org.workswap.notification.dto.FullNotificationDTO;
import org.workswap.notification.dto.NotificationDTO;
import org.workswap.notification.enums.NotificationType;
import org.workswap.notification.services.NotificationCommandService;
import org.workswap.notification.services.NotificationMappingService;
import org.workswap.order.datasource.model.Order;
import org.workswap.shared.enums.Importance;
import org.workswap.shared.events.notification.CreateNotificationCommand;
import org.workswap.shared.util.WebhookSigner;
import org.workswap.user.datasource.model.User;
import org.workswap.user.datasource.repository.UserRepository;
import org.salavion.security.dto.UserAuthData;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Profile({"production"})
public class NotificationCommandServiceImpl implements NotificationCommandService {
    
    private static final Logger logger = LoggerFactory.getLogger(NotificationCommandService.class);

    private final NotificationRepository notificationRepository;

    /* private final NewsService newsService; */
    private final NotificationMappingService mappingService;
    private final SimpMessagingTemplate messagingTemplate;

    private final UserRepository userRepository;

    @Value("${tgbot.url}")
    private String tgBotUrl;

    public FullNotificationDTO saveNotification(CreateNotificationCommand event) {

        User reciver = userRepository.getReferenceById(event.userId());

        Notification notification = new Notification(
            reciver, 
            event.title(), 
            event.content(), 
            event.link(), 
            NotificationType.CHAT, 
            Importance.INFO);

        Notification saved = notificationRepository.save(notification);

        FullNotificationDTO fullNotification = mappingService.toDTO(saved);

        return fullNotification;
    }

    /* public void sendNewsNotification(News news) {
        List<User> reciverList = userRepository.findAll(); 

        for(User receiver : reciverList) {

            Locale reciverLocale = Locale.of("en");
            
            if (!receiver.getLanguages().isEmpty()) {
                logger.debug("У пользователя найдено языков: {}", receiver.getLanguages());
                logger.debug("Берём язык: {}", receiver.getLanguages().get(0));
                reciverLocale = Locale.of(receiver.getLanguages().get(0));
            }

            newsService.localizeNews(news, reciverLocale);

            NotificationDTO notification = new NotificationDTO(
                messageSource.getMessage("new.news.notification", null, reciverLocale),
                news.getLocalizedTitle(),
                "/news/" + news.getId()
            );
            saveChatNotification(receiver.getId(), notification);
        }
    } */

    public void markAsRead(UserAuthData authData, Long notificationId) {
        notificationRepository.markAsRead(notificationId, authData.id());
    }

    public void sendOrderCompleteNotification(Order order) {
        
        List<User> users = new ArrayList<>(List.of(order.getSeller(), order.getBuyer()));
        String link = "/account/orders";
        String title = "Заказ завершён!";

        String message = "Заказ #" + order.getId() + " завершен. Обе стороны подтвердили выполнение заказа.";

        for(User user : users) {
            Notification notification = new Notification(
                user,
                title, 
                message, 
                link, 
                NotificationType.CHAT, 
                Importance.INFO
            );
            
            notificationRepository.save(notification);
        }
    }

    public void sendNotificationToTelegram(User user, NotificationDTO dto) {
        try {

            logger.error("Начинаем отправлять уведомление в телеграм");
            // Строим JSON вручную или через ObjectMapper
            ObjectMapper objectMapper = new ObjectMapper();

            Map<String, Object> metadata = new HashMap<>();
            metadata.put("messageId", UUID.randomUUID().toString());

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("userId", user.getEmail());
            requestBody.put("message", dto.content());
            requestBody.put("link", "https://workswap.org" + dto.link()); // добавил это
            requestBody.put("type", "info");
            requestBody.put("metadata", metadata);

            String json = objectMapper.writeValueAsString(requestBody);
            String signature = WebhookSigner.generateSignature(json);

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(tgBotUrl + "/api/notifications/send"))
                .header("Content-Type", "application/json")
                .header("X-Webhook-Signature", signature)
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();

            HttpClient client = HttpClient.newHttpClient();
            client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    logger.debug("Notification sent. Status: {}", response.statusCode());
                    logger.debug("Response: {}", response.body());
                });

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Failed to send notification to Telegramm: {}", e.getMessage());
        }
    }

    public void sendNotification(CreateNotificationCommand event) {

        // Функция должна сохранять уведомление, но для чата это не требуется
        // надо переписать метод, чтобы у каждой сущности был свой метод сохранения уведомления, в этот метод просто отправлял уведмоление
        // Сейчас этот метод просто перенаправляет на сохранение в телеграм

        FullNotificationDTO fullNotification = saveNotification(event);

        if (fullNotification == null) {
            throw new IllegalStateException("Ошибка сохранения уведомления!");
        }
       
        User receiver = userRepository.getReferenceById(event.userId());

        String receiverOpenId = receiver.getOpenId();

        if (receiverOpenId == null) {
            throw new IllegalStateException("У пользователя нет почты!");
        }

        messagingTemplate.convertAndSendToUser(
                receiverOpenId,
                "/queue/notifications",
                fullNotification
        );
    }
}
