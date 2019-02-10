package com.example.todo.exception;

public class InvalidSearchFieldException  extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidSearchFieldException(String msg) {
		super(msg);

	}
	
	public InvalidSearchFieldException(String msg, Throwable throwable) {
		super(msg, throwable);

	}

}
