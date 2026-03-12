package org.workswap.task.services.impl;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.workswap.shared.dto.Status;
import org.workswap.task.datasource.model.Task;
import org.workswap.task.datasource.model.TaskComment;
import org.workswap.task.dto.TaskCommentDTO;
import org.workswap.task.dto.TaskDTO;
import org.workswap.task.services.TaskMappingService;
import org.workswap.user.datasource.repository.UserRepository;
import org.workswap.user.dto.ShortUserDTO;
import org.workswap.user.services.UserMappingService;

import lombok.RequiredArgsConstructor;

@Service
@Profile("backoffice")
@RequiredArgsConstructor
public class TaskMappingServiceImpl implements TaskMappingService {
    
    private final UserMappingService userMappingService;
    private final UserRepository userRepository;

    public TaskDTO toDTO(Task task) {

        Long executorId = task.getExecutorId();
        Long authorId = task.getAuthorId();

        ShortUserDTO executor = null;
        if (executorId != null) {
            executor = userMappingService.toShortDTO(userRepository.findById(executorId).orElse(null));
        }

        ShortUserDTO author = null;
        if (authorId != null) {
            author = userMappingService.toShortDTO(userRepository.findById(authorId).orElse(null));
        }

        Status status = new Status(task.getStatus().getDisplayName(), task.getStatus().toString());

        return new TaskDTO(
            task.getId(),
            task.getName(),
            task.getDescription(),
            status,
            task.getTaskType().getDisplayName(),
            author,
            executor,
            task.getCreatedAt(),
            task.getDeadline(),
            task.getCompleted()
        );
    }

    public TaskCommentDTO convertCommentToDto(TaskComment comment) {

        Long authorId = comment.getAuthorId();

        ShortUserDTO author = null;
        if (authorId != null) {
            author = userMappingService.toShortDTO(userRepository.findById(authorId).orElse(null));
        }

        return new TaskCommentDTO(
            comment.getId(),
            comment.getTask().getId(),
            comment.getContent(),
            comment.getCreatedAt(),
            author
        );
    }
}
