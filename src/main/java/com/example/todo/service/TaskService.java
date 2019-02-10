package com.example.todo.service;

import java.util.Collection;

import org.springframework.data.jpa.domain.Specification;

import com.example.todo.model.Task;
import com.example.todo.model.TaskDonePartial;

public interface TaskService {
	   public abstract Task createTask(Task task);
	   public abstract Task updateTask(Long id, Task task); 
	   public abstract Task updateTask(Long id, TaskDonePartial task);
	   public abstract void deleteTask(Long id);
	   public abstract Collection<Task> findTasks(Specification<Task> spec);
	   public abstract Task findTaskById(Long id);
	   
	   
	}