package com.example.todo.service;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import com.example.todo.model.Task;
import com.example.todo.model.TaskDonePartial;
import com.example.todo.repository.TaskRepository;

@Service
public class TaskServiceImpl implements TaskService {
	
	@Autowired
	TaskRepository taskRepository;
	
	@Override
	public Task createTask(Task task) {
		
		return taskRepository.save(task);	
	}

	@Override
	public Task updateTask(Long id, Task updatedTask) {
		Task task = taskRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Task not found with id " + id));
		
		task.setName(updatedTask.getName());
		task.setDescription(updatedTask.getDescription());
		task.setDone(updatedTask.getDone());

		return taskRepository.save(task);
	}

	@Override
	public void deleteTask(Long id) {
			
		Task task = taskRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Task not found with id " + id));
		taskRepository.delete(task);
	}

	@Override
	public Collection<Task> findTasks(Specification<Task> spec) {
		return taskRepository.findAll(spec);
	}
	
	@Override
	public Task findTaskById(Long id) {
		Task task = taskRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Task not found with id " + id));
		return task;
	}

	@Override
	public Task updateTask(Long id, TaskDonePartial taskPartial) {
		Task task = taskRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Task not found with id " + id));
		task.setDone(taskPartial.getDone());
		
		return taskRepository.save(task);
	}
}
