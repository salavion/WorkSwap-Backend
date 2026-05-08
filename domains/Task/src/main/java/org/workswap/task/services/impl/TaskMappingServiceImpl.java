package org.workswap.task.services.impl;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.workswap.shared.dto.Status;
import org.workswap.task.datasource.model.Task;
import org.workswap.task.datasource.model.TaskComment;
import org.workswap.task.dto.TaskCommentDTO;
import org.workswap.task.dto.TaskDTO;
import org.workswap.task.services.TaskMappingService;
import org.workswap.user.dto.ShortUserDTO;
import org.workswap.user.services.UserQueryService;

import lombok.RequiredArgsConstructor;

@Service
@Profile("backoffice")
@RequiredArgsConstructor
public class TaskMappingServiceImpl implements TaskMappingService {

    private final UserQueryService userQueryService;

    public TaskDTO toDTO(Task task) {

        Long executorId = task.getExecutorId();
        Long authorId = task.getAuthorId();

        ShortUserDTO executor = null;
        if (executorId != null) {
            executor = userQueryService.getById(executorId);
        }

        ShortUserDTO author = null;
        if (authorId != null) {
            author = userQueryService.getById(authorId);
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
            author = userQueryService.getById(authorId);
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
