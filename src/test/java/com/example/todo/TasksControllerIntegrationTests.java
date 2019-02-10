package com.example.todo;

/**
 * Task API Integration tests using Mockmvc
 */

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import com.example.todo.model.Task;
import com.example.todo.repository.TaskRepository;
import com.example.todo.service.TaskServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(classes = TodoApplication.class)
@RunWith(SpringJUnit4ClassRunner.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class TasksControllerIntegrationTests {
	
	private MockMvc mvc;

	@Autowired
	TaskServiceImpl taskService;

	@Autowired
	TaskRepository taskRepository;
	
	@Autowired
	private WebApplicationContext webApplicationContext;

	private List<Task> taskList = new ArrayList<>();
	   
	@Before
	public void setup() {

		mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

		Task task1 = new Task(1L, "Test Task 1");
		Task task2 = new Task(2L, "Test Task 2");
		taskList.add(task1);
		taskList.add(task2);
	}
	   
	@Test
	public void when_ListTasks_Expect_TwoItemsOK() throws Exception {
		
		taskService.createTask(new Task(1L, "Task Name 1"));
		taskService.createTask(new Task(2L, "Task Name 1"));

		this.mvc.perform(get("/api/v1/tasks")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				)
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.length()").value(2));
	}
	
	@Test
	public void when_getTaskValidId_Expect_OK() throws Exception {
		
		Task task = taskService.createTask(new Task(1L, "Task Name 1", "Some Description"));
		
		this.mvc.perform(get("/api/v1/tasks/{id}", task.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				)
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.name", is("Task Name 1")))
		.andExpect(jsonPath("$.description", is("Some Description")))
		.andExpect(jsonPath("$.createdAt").exists())
		.andExpect(jsonPath("$.done").exists());
	}
	
	@Test
	public void when_PostTaskValid_Expect_OK() throws Exception {
		
		Task task = new Task(1L, "Task Name 1");

		this.mvc.perform(post("/api/v1/tasks")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(task))
				)
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.name").value("Task Name 1"));
	}
	
	
	   @Test
	   public void when_PutTaskValid_Expect_Ok() throws Exception {
		   
		   Task task = taskService.createTask(new Task(1L, "Task"));
		   
		   JSONObject json = new JSONObject();
		   json.put("name", "Test Task 1");
		   json.put("description", "Valid Description");
		   
		    this.mvc.perform(put("/api/v1/tasks/{id}", task.getId())
		    		.content(json.toString())
		    		.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
		    .andExpect(jsonPath("$.name").value("Test Task 1"));
	   }
	
   
   @Test
   public void when_DeleteTask_Expect_OK() throws Exception {
	   
	   Task task = taskService.createTask(new Task(1L, "Task Name 1"));
	   
	    this.mvc.perform(delete("/api/v1/tasks/{id}", task.getId())
	            .contentType(MediaType.APPLICATION_JSON)
	            .accept(MediaType.APPLICATION_JSON))	
	            .andExpect(status().isOk());
   }

	   
}
