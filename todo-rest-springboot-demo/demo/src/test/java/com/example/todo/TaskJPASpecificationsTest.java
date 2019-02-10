package com.example.todo;


/**
 * TaskJPASpecification tests for searching tasks.
 * 
 */
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isIn;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.example.todo.model.Task;
import com.example.todo.repository.TaskRepository;
import com.example.todo.specification.SearchCriteria;
import com.example.todo.specification.TaskSpecification;
import com.example.todo.specification.TaskSpecificationBuilder;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = TodoApplication.class)
@Transactional
@ActiveProfiles("test")
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
public class TaskJPASpecificationsTest {

	@Autowired
	private TaskRepository taskRepository;

	private Task task1;
	private Task task2;

	@Before
	public void init() {

		task1 = new Task();
		task1.setName("Task 1");
		task1.setDescription("Task 1 Description");
		taskRepository.save(task1);


		task2 = new Task();
		task2.setName("Task 2");
		task2.setDescription("Task 2 Description");
		taskRepository.save(task2);
		
	}
	
	@Test
	public void when_BothMatchBeginOfName_Expect_Both() {
	   TaskSpecification spec = 
	      new TaskSpecification(new SearchCriteria("name", ":", "Task"));
	     
	    List<Task> results = taskRepository.findAll(spec);
	    assertThat(task1, isIn(results));
	    assertThat(task2, isIn(results));
	}
	
	@Test
	public void when_NameTask1_Expect_OnlyTask1() {
	   TaskSpecification spec = 
	      new TaskSpecification(new SearchCriteria("name", ":", "Task 1"));
	     
	    List<Task> results = taskRepository.findAll(spec);
	    assertThat(task1, isIn(results));
	    assertThat(task2, not(isIn(results)));
	}
	
	@Test
	public void when_DoneFalse_Expect_Both() {
	   TaskSpecification spec = 
	      new TaskSpecification(new SearchCriteria("done", ":", false));
	     
	    List<Task> results = taskRepository.findAll(spec);
	    assertThat(task1, isIn(results));
	    assertThat(task2, isIn(results));
	}
	
	@Test
	public void when_InvalidName_Expect_EmptyList() {
	   TaskSpecification spec = 
	      new TaskSpecification(new SearchCriteria("name", ":", "INVLID"));
	     
	    List<Task> results = taskRepository.findAll(spec);
	    assertThat(task1, not(isIn(results)));
	    assertThat(task2, not(isIn(results)));
	}
	
	@Test
	public void when_IdGreaterThan1_Expect_OneItem() {
	   TaskSpecification spec = 
	      new TaskSpecification(new SearchCriteria("id", ">", 1));

	    List<Task> results = taskRepository.findAll(spec);
	    assertThat(results.size(), is(1));
	}
	
	@Test
	public void when_IdGreaterThan1AndNameMatch_Expect_OneItem() {
		TaskSpecificationBuilder builder = new TaskSpecificationBuilder();
		builder.with("id", ">", 1);
		builder.with("name", ":", "Task");

		Specification<Task> spec = builder.build();

		List<Task> results = taskRepository.findAll(spec);
		assertThat(results.size(), is(1));
	}

}