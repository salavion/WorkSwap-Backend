package org.workswap.task.services;

import org.workswap.task.datasource.model.Task;
import org.workswap.task.datasource.model.TaskComment;
import org.workswap.task.dto.TaskCommentDTO;
import org.workswap.task.dto.TaskDTO;

public interface TaskMappingService {

    TaskDTO toDTO(Task task);
    TaskCommentDTO convertCommentToDto(TaskComment comment);
}
