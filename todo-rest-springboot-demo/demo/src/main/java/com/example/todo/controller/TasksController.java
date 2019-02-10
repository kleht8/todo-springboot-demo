package com.example.todo.controller;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.todo.exception.InvalidSearchFieldException;
import com.example.todo.model.Task;
import com.example.todo.model.TaskDonePartial;
import com.example.todo.service.TaskService;
import com.example.todo.specification.TaskSpecificationBuilder;

@RestController
@RequestMapping(value = "/api/v1", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class TasksController {
	
	private final static Logger LOG = LoggerFactory.getLogger(TasksController.class);
	
	private TaskService taskService;
	
	@Autowired
	public TasksController(TaskService taskService) {
		this.taskService = taskService;
	}
	
	
    @RequestMapping(value = "/tasks", method = RequestMethod.POST)
	public ResponseEntity<Task> postTask(@Valid @RequestBody Task task)  {

		// Save to DB
		Task newTask = taskService.createTask(task);

		return new ResponseEntity<Task>(newTask, HttpStatus.OK);
	}
    
    /**
     * Gets list of tasks, allows filtering by using Query Language
     * @param search
     * @return List of Tasks
     * @throws SecurityException
     * @throws InvalidSearchFieldException
     */
    @RequestMapping(value = "/tasks", method = RequestMethod.GET)
	public ResponseEntity<Collection<Task>> listTasks(@RequestParam(value = "search", required = false) String search) throws SecurityException, InvalidSearchFieldException {

		Specification<Task> spec = resolveSpecification(search);
		Collection<Task> taskList = taskService.findTasks(spec);

		return new ResponseEntity<Collection<Task>>(taskList, HttpStatus.OK);
	}
    
    @RequestMapping(value = "/tasks/{id}", method = RequestMethod.GET)
	public ResponseEntity<Task> getTask(@PathVariable(name = "id", required = true) Long id) {

		Task task = taskService.findTaskById(id);

		return new ResponseEntity<Task>(task, HttpStatus.OK);
	}
    
    /**
     * Updates task object
     * 
     * @param id
     * @param task
     * @return Updated Task
     */
    @RequestMapping(value = "/tasks/{id}", method = RequestMethod.PUT)
	public ResponseEntity<Task> updateTask(
			@PathVariable(name = "id", required = true) Long id,
			@Valid @RequestBody Task task) {

		Task updatedTask = taskService.updateTask(id, task);

		return new ResponseEntity<Task>(updatedTask, HttpStatus.OK);
	}
    
    /**
     * Updates done -attribute true or false
     * @param id
     * @param Partial task
     * @return Updated Task
     */
    @RequestMapping(value = "/tasks/{id}", method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Task> PatchTaskDone(
			@PathVariable(name = "id", required = true) Long id,
			@Valid @RequestBody TaskDonePartial task) {

		Task updatedTask = taskService.updateTask(id, task);

		return new ResponseEntity<Task>(updatedTask, HttpStatus.OK);
	}
    
    
    @RequestMapping(value = "/tasks/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    public void deleteTask(@PathVariable (name = "id", required = true) Long id) {
    	taskService.deleteTask(id);    	
    }
    
    
    /**
     * Build Specification from searchParameters. Throws exception if searchable field does not exists in Task Class
     * @param searchParameters
     * @return Specification<Task> 
     * @throws SecurityException
     * @throws InvalidSearchFieldException
     */
    protected Specification<Task> resolveSpecification(String searchParameters) throws SecurityException, InvalidSearchFieldException {
    	   	
    	TaskSpecificationBuilder builder = new TaskSpecificationBuilder();
        Pattern pattern = Pattern.compile("(\\w+?)(:|<|>)(.+?),");
        Matcher matcher = pattern.matcher(searchParameters + ",");
        while (matcher.find()) {
        	
        	// Check that Task.class has given field to search
        	try  {
        		Task.class.getDeclaredField(matcher.group(1));
        	} catch (NoSuchFieldException e) {
        		throw new InvalidSearchFieldException(String.format("Invalid search term %s", matcher.group(1)));
        	}
        		
            builder.with(matcher.group(1), matcher.group(2), matcher.group(3));
        }
        
       return builder.build();
    	
    }
  }
