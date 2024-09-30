package com.aegis.project.service;

import java.util.Date;

import com.aegis.project.model.TaskModel;
import com.aegis.project.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    public boolean createTask(int parentProjectID, int parentOrgID, String taskName, String taskDescription, int assignerID, String taskPriority, Date dueDate) {
        TaskModel task = new TaskModel();
        task.setParentProjectID(parentProjectID);
        task.setParentOrgID(parentOrgID);
        task.setTaskName(taskName);
        task.setTaskDescription(taskDescription);
        task.setAssignerID(assignerID);
        task.setTaskPriority(taskPriority);
        task.setDueDate(dueDate);

        taskRepository.save(task);
        return true;
    }
}
