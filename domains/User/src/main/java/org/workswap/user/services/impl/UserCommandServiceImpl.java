package org.workswap.user.services.impl;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.EntityNotFoundException;

import org.workswap.user.services.UserCommandService;
/* import org.workswap.core.common.utils.WebhookSigner;
import org.workswap.datasource.central.model.listing.Listing;
import org.workswap.datasource.central.model.Notification;
import org.workswap.datasource.central.model.Review;
import org.workswap.datasource.central.model.chat.Chat;
import org.workswap.datasource.central.model.chat.ChatParticipant;
import org.workswap.datasource.central.model.listing.Location;
import org.workswap.datasource.central.repository.listing.ListingRepository;
import org.workswap.datasource.central.repository.listing.LocationRepository;
import org.workswap.datasource.central.repository.NotificationRepository;
import org.workswap.datasource.central.repository.ReviewRepository;
import org.workswap.datasource.central.repository.chat.ChatRepository; */
import org.workswap.user.datasource.model.permission.Role;
import org.workswap.location.datasource.model.Location;
import org.workswap.location.datasource.repository.LocationRepository;
import org.workswap.user.datasource.model.User;
import org.workswap.user.datasource.model.UserSettings;
import org.workswap.user.datasource.repository.permission.RoleRepository;
import org.workswap.user.datasource.repository.UserRepository;
import org.salavion.security.dto.UserAuthData;
import org.salavion.security.dto.UserInfoDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Profile("production")
public class UserCommandServiceImpl implements UserCommandService {

    private static final Logger logger = LoggerFactory.getLogger(UserCommandService.class);

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;

    /* private final ChatRepository chatRepository;
    private final ListingRepository listingRepository;
    private final ReviewRepository reviewRepository;
    private final NotificationRepository notificationRepository; */

    @Value("${salavion.url}")
    private String authServiceUrl;

    @Value("${salavion.code}")
    private String salavionCode;

    @Transactional
    public void deleteUser(UserAuthData authData) {
        User user = userRepository.findById(authData.id()).orElseThrow(
            () -> new EntityNotFoundException("Пользователь не найден"));

        try {
            if (user == null) {
                throw new RuntimeException("Пользователя не зарегистрировано.");
            }

            logger.debug("Пользователь {} найден, начинаем удаление", user.getId());

            // Чистка разговоров

            /* Set<ChatParticipant> chatParticipants = new HashSet<>(user.getChatParticipants());
            logger.debug("> Чистка чатов");

            if (!chatParticipants.isEmpty()) {
                for (ChatParticipant chatParticipant : chatParticipants) {
                    Chat chat = chatParticipant.getChat();
                    user.getChatParticipants().remove(chatParticipant);
                    logger.debug(">> Удаление чата {}", chat.getId());
                    chatRepository.delete(chat);
                }
            } else {
                logger.debug(">> У пользователя не найдено чатов");
            }

            // Чистка объявлений
            List<Listing> listings = new ArrayList<>(user.getListings());
            logger.debug("> Чистка объявлений");

            if (!listings.isEmpty()) {
                for (Listing listing : listings) {
                    logger.debug(">> Удаление объявления {}", listing.getId());
                    user.getListings().remove(listing);
                    listingRepository.delete(listing);
                }
            } else {
                logger.debug(">> У пользователя не найдено объявлений");
            }

            // Чистка отзывов
            List<Review> reviews = new ArrayList<>(user.getReviews());
            logger.debug("> Чистка отзывов");

            if (!reviews.isEmpty()) {
                for (Review review : reviews) {
                    logger.debug(">> Удаление отзыва {}", review.getId());
                    user.getReviews().remove(review);
                    reviewRepository.delete(review);
                }
            } else {
                logger.debug(">> У пользователя не найдено отзывов");
            }

            // Чистка уведомления
            List<Notification> notifications = new ArrayList<>(notificationRepository.findByRecipient(user));
            logger.debug("> Чистка уведомлений");

            if (!notifications.isEmpty()) {
                logger.debug(">> Удаляем {} уведомлений", notifications.size());
                notificationRepository.deleteAll(notifications);
            } else {
                logger.debug(">> У пользователя не найдено уведомлений");
            } */

            // Удаление пользователя
            try {
                Long userId = user.getId();

                if (userId == null) {
                    throw new RuntimeException("Id пользователя не найдено!");
                }
                logger.debug("Удаление пользователя {}", userId);
                userRepository.deleteById(userId);
            } catch (Exception e) {
                logger.error("Ошибка при удалении пользователя {}: {}", user.getId(), e.getMessage(), e);
                throw new RuntimeException("Ошибка при удалении пользователя", e);
            }

        } catch (Exception e) {
            logger.error("Не удалось удалить пользователя через OAuth2: {}", e.getMessage(), e);
            throw new RuntimeException("Ошибка при удалении пользователя через OAuth2", e);
        }
    }

    public void modifyUserParam(UserAuthData authData, Map<String, Object> updates) {

        User user = userRepository.findByIdWithSettings(authData.id());
        UserSettings settings = user.getSettings();

        if (user != null) {
            updates.forEach((key, value) -> {
                switch (key) {
                    case "name":
                        user.setName((String) value);
                        break;
                    case "phone":
                        user.setPhone((String) value);
                        break;
                    case "phoneVisible":
                        settings.setPhoneVisible((Boolean) value);
                        break;
                    case "emailVisible":
                        settings.setEmailVisible((Boolean) value);
                        break;
                    case "avatarType":
                        String avatarType = (String) value;
                        settings.setAvatarType(avatarType);
                        switch (avatarType) {
                            case "google":
                                user.setAvatarUrl(settings.getGoogleAvatar());
                                break;
                            case "uploaded":
                                user.setAvatarUrl(settings.getUploadedAvatar());
                                break;
                            case "default":
                                user.setAvatarUrl("/images/avatar-placeholder.png");
                                break;
                        
                            default:
                                break;
                        }
                        break;
                    case "languages":
                        if (value instanceof List<?> listValue) {
                            user.setLanguages(listValue.stream()
                                .filter(item -> item instanceof String)
                                .map(item -> (String) item)
                                .toList()
                            );
                        } else {
                            logger.warn("Ожидался список, но получили: {}", value.getClass().getName());
                        }
                        break;
                    case "locationId":
                        if (value != null) {
                            Long locId = ((Number) value).longValue(); // безопасно для Integer и Long
                            Location loc = locationRepository.findById(locId).orElse(null);
                            user.setLocation(loc);
                        }
                        break;
                    case "bio":
                        user.setBio((String) value);
                        break;
                }
            });
        }

        userRepository.save(user);
    }

    public String connectTelegram(UserAuthData authData) {
        User user = userRepository.findByIdWithSettings(authData.id());
        String email = user.getEmail();

        String body = "{\"websiteUserId\":\"" + email + "\"}";
        String signature = /* WebhookSigner.generateSignature(body); */ "";

        try {
            HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://89.35.130.223:30003/api/users/generate-token"))
                .header("Content-Type", "application/json")
                .header("X-Webhook-Signature", signature)
                .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode json = objectMapper.readTree(response.body());

            String linkUrl = json.path("data").path("linkUrl").asText();

            user.getSettings().setTelegramConnected(true);
            userRepository.save(user);

            return linkUrl;

        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Ошибка при отправке запроса");
        }
    }

    public void acceptTerms(UserAuthData authData) {
        User user = userRepository.findById(authData.id()).orElseThrow(
            () -> new EntityNotFoundException("Пользователь не найден"));

        user.setTermsAcceptanceDate(LocalDateTime.now());
        user.setTermsAccepted(true);
        
        userRepository.save(user);
    }

    public void createUser(@NonNull Long userId) {

        if (userRepository.existsById(userId)) {
            return;
        }

        WebClient client = WebClient.create(Objects.requireNonNull(authServiceUrl));

        UserInfoDTO info = client.get()
                .uri("/api/user/info/" + userId)
                .header("X-SALAVION-CODE", salavionCode)
                .retrieve()
                .bodyToMono(UserInfoDTO.class)
                .block();

        Role role = roleRepository.findByName("TEMP_USER");
        User user = new User(
            info.id(),
            info.openId(),
            info.name(), 
            info.email(), 
            info.avatarUrl(), 
            Set.of(role), 
            info.status());
        userRepository.save(user);
    }
}
