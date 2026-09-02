package org.workswap.rabbit.queues.events;

public record UserCreatedEvent(
    Long id,
    String openId,
    String name,
    String email,
    String avatarUrl,
    String status
) {
}