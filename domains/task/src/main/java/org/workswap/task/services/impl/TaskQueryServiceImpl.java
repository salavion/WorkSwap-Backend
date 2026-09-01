package org.workswap.task.services.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.workswap.sso.security.dto.UserAuthData;
import org.workswap.task.datasource.model.Task;
import org.workswap.task.datasource.repository.TaskCommentRepository;
import org.workswap.task.datasource.repository.TaskRepository;
import org.workswap.task.dto.TaskCommentDTO;
import org.workswap.task.dto.TaskDTO;
import org.workswap.task.dto.TasksPageRequest;
import org.workswap.task.dto.UserTasksTable;
import org.workswap.task.enums.TaskStatus;
import org.workswap.task.services.TaskMappingService;
import org.workswap.task.services.TaskQueryService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TaskQueryServiceImpl implements TaskQueryService{
    
    private final TaskRepository taskRepository;
    private final TaskMappingService taskMappingService;
    private final TaskCommentRepository taskCommentRepository;

    public TasksPageRequest getTasksPage(UserAuthData authData) {

        Pageable pageable = PageRequest.of(0, 15);
        Page<Task> newTasks = taskRepository.findPageWithUsersFiltered(TaskStatus.NEW, null, "created", pageable);
        List<Task> executing = taskRepository.findByExecutorIdAndStatus(authData.id(), TaskStatus.IN_PROGRESS);
        List<Task> completed = taskRepository.findCompletedAfter(authData.id(), LocalDateTime.now().minusMonths(1));
        long completedBefore = taskRepository.countCompletedBefore(authData.id(), LocalDateTime.now().minusMonths(1));

        UserTasksTable userTasks = new UserTasksTable(
            executing.stream().map(t -> taskMappingService.toDTO(t)).toList(), 
            completed.stream().map(t -> taskMappingService.toDTO(t)).toList(), 
            completedBefore
        );

        Page<TaskDTO> list = newTasks.map(taskMappingService::toDTO);
        return new TasksPageRequest(userTasks, list);
    }

    public Task getTaskById(Long taskId) {
        if (taskId == null) {
            throw new IllegalArgumentException("ID задачи не указано");
        }

        return taskRepository.findById(taskId).orElse(null);
    }

    public TaskDTO getTaskDetails(Long taskId) {
        TaskDTO task = taskMappingService.toDTO(getTaskById(taskId));

        /* ShortUserDTO executor = null;

        Long executorId = task.executorId();
        if (executorId != null) {
            executor = userMappingService.toShortDTO(userRepository.findById(executorId).orElse(null));
        }

        Long authorId = task.authorId();

        ShortUserDTO author = null;

        if (authorId != null) {
            author = userMappingService.toShortDTO(userRepository.findById(authorId).orElse(null));
        }
        
        if (executor != null) task.setExecutor(executor);
        if (author != null) task.setAuthor(author);
         */
        return task;
    }

    public List<TaskCommentDTO> getTaskComments(Long taskId) {
        return taskCommentRepository.findAllByTaskId(taskId)
                                    .stream()
                                    .map(comment -> taskMappingService.convertCommentToDto(comment))
                                    .toList();
    }
}
