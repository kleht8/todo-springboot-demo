package com.example.todo;

/**
 * Task Controller Unit tests using Mockmvc
 */

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.example.todo.model.Task;
import com.example.todo.model.TaskDonePartial;
import com.example.todo.repository.TaskRepository;
import com.example.todo.service.TaskServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(classes = TodoApplication.class)
@RunWith(SpringJUnit4ClassRunner.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class TasksControllerUnitTests {
	
	private MockMvc mvc;

	@MockBean
	TaskServiceImpl taskService;

	@MockBean
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
		
		when(taskService.findTasks(null)).thenReturn(taskList);

		this.mvc.perform(get("/api/v1/tasks")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				)
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.length()").value(2));
	}
	
	@Test
	public void when_getTaskValidId_Expect_OK() throws Exception {
		
		when(taskService.findTaskById(Mockito.anyLong())).thenReturn(new Task(1L, "Task 1", "Description", true));

		this.mvc.perform(get("/api/v1/tasks/{id}", 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				)
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.name", is("Task 1")))
		.andExpect(jsonPath("$.id", is(1)))
		.andExpect(jsonPath("$.description", is("Description")))
		.andExpect(jsonPath("$.createdAt").exists())
		.andExpect(jsonPath("$.done").exists());
	}
	
	@Test
	public void when_getTaskInvalidId_Expect_NotFound() throws Exception {
		
		when(taskService.findTaskById(Mockito.anyLong())).thenThrow(new ResourceNotFoundException());

		this.mvc.perform(get("/api/v1/tasks/{id}", 123)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				)
		.andExpect(status().isNotFound());
	}
	

	@Test
	public void when_SearchTasksWithInvalidTerm_Expect_BadRequest() throws Exception {

		this.mvc.perform(get("/api/v1/tasks?search=Invalid:something")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				)
				.andExpect(status().isBadRequest())
				.andDo(print())
				.andReturn();
	}	
	
	@Test
	public void when_SearchTasksWithValidTermName_Expect_Ok() throws Exception {

		this.mvc.perform(get("/api/v1/tasks?search=name:Task 1")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				)
				.andExpect(status().isOk());
	}
	   
	@Test
	public void when_PostTaskValid_Expect_OK() throws Exception {
		   
	   Task task = new Task(1L, "Test Task", "Test Description");
	   when(taskService.createTask(Mockito.any(Task.class))).thenReturn(task);
	     
	    this.mvc.perform(post("/api/v1/tasks")
	    		.content(new ObjectMapper().writeValueAsString(task))
	    		.contentType(MediaType.APPLICATION_JSON)
	    		.accept(MediaType.APPLICATION_JSON)
	    		)
	    .andExpect(status().isOk())
	    .andExpect(jsonPath("$.id", is(1)))
	    .andExpect(jsonPath("$.name", is("Test Task")))
	    .andExpect(jsonPath("$.description", is("Test Description")))
	    .andDo(print()).andReturn();
		   
	}
	   
	   @Test
	   public void when_PostTaskMissingName_Expect_BadRequest() throws Exception {
		   
		   JSONObject json = new JSONObject();
		   json.put("description", "Valid Description");
		   json.put("done", false);
		   
		    this.mvc.perform(post("/api/v1/tasks")
		    		.content(json.toString())
		    		.contentType(MediaType.APPLICATION_JSON)
		    		.accept(MediaType.APPLICATION_JSON)
		    		)
		    .andExpect(status().isBadRequest());
		   
	   }

	   @Test
	   public void when_PostTaskNameTooShort_Expect_BadRequest() throws Exception {
		   
		   JSONObject json = new JSONObject();
		   json.put("name", "123");
		   json.put("description", "Valid Description");
		   json.put("done", false);
		   
		    this.mvc.perform(post("/api/v1/tasks")
		    		.content(json.toString())
		    		.contentType(MediaType.APPLICATION_JSON)
		    		.accept(MediaType.APPLICATION_JSON)
		    		)
		    .andExpect(status().isBadRequest());
	   }
	   
	   @Test
	   public void when_PostTaskNameTooLong_Expect_BadRequest() throws Exception {
		   
		   JSONObject json = new JSONObject();
		   json.put("name", "This is going to be over 64 characters long, This is going to be over 64 characters long");
		   json.put("description", "Valid Description");
		   
		    this.mvc.perform(post("/api/v1/tasks")
		    		.content(json.toString())
		    		.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
	   }
	   
	   @Test
	   public void when_PutTaskValid_Expect_Ok() throws Exception {
	   
		   Task task = new Task(1L, "Test Task 1", "Valid Description");
		   when(taskService.updateTask(Mockito.anyLong(), Mockito.any(Task.class))).thenReturn(task);
		   
		   JSONObject json = new JSONObject();
		   json.put("name", "Test Task 1");
		   json.put("description", "Valid Description");
		   
		    this.mvc.perform(put("/api/v1/tasks/1")
		    		.content(json.toString())
		    		.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
		    .andExpect(jsonPath("$.name").value("Test Task 1"));
	   }

	   
	   @Test
	   public void when_PutTaskNameTooShort_Expect_BadRequest() throws Exception {
		   
		  Task task = new Task(1L, "123", "12345");
		   
		    this.mvc.perform(post("/api/v1/tasks")
		    		.content(new ObjectMapper().writeValueAsString(task))
		    		.contentType(MediaType.APPLICATION_JSON)
		    		.accept(MediaType.APPLICATION_JSON)
		    		)
		    .andExpect(status().isBadRequest())
		    .andExpect(jsonPath("$.error").exists());
	   }
	   
	   @Test
	   public void when_PatchChangeDoneToTrue_Expect_OK() throws Exception {
		   

		   Task doneTask = new Task(1L, "Test Task", "Test Description", true);	   
		   when(taskService.updateTask(Mockito.any(), Mockito.any(TaskDonePartial.class))).thenReturn(doneTask);

		   JSONObject json = new JSONObject();
		   json.put("done", true);

		    this.mvc.perform(patch("/api/v1/tasks/1")
		    		.content(json.toString())
		    		.contentType(MediaType.APPLICATION_JSON)
		    		.accept(MediaType.APPLICATION_JSON)
		    		)
		    
		    .andExpect(status().isOk())
		    .andExpect(jsonPath("$.done", is(true)))
		    .andExpect(jsonPath("$.name", is("Test Task")))
		    .andDo(print());
	   }
	   
	   @Test
	   public void when_DeleteTask_Expect_OK() throws Exception {
		   
		    this.mvc.perform(delete("/api/v1/tasks/{id}", 1L)
		            .contentType(MediaType.APPLICATION_JSON)
		            .accept(MediaType.APPLICATION_JSON))	
		            .andExpect(status().isOk());
	   }

	   
}
