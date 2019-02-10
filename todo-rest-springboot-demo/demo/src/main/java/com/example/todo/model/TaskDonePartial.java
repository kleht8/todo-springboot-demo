package com.example.todo.model;


import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TaskDonePartial {

	@NotNull
	private Boolean done;
	
	

	

}
