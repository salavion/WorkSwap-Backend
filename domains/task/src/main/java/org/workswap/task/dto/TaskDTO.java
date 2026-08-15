package org.workswap.task.dto;

import java.time.LocalDateTime;

import org.workswap.shared.dto.Status;
import org.workswap.user.dto.ShortUserDTO;

public record TaskDTO(
    Long id,
    String name,
    String description,

    Status status,
    String type,

    ShortUserDTO author,
    ShortUserDTO executor,

    LocalDateTime createdAt,
    LocalDateTime deadline,
    LocalDateTime completed
) {}
