package com.example.todo.exception;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.util.WebUtils;

import com.example.todo.model.ApiError;

@ControllerAdvice
public class GlobalControllerExceptionHandler extends ResponseEntityExceptionHandler {
	
	 private final static Logger LOG = LoggerFactory.getLogger(GlobalControllerExceptionHandler.class);
	 
	 
	// Generic error response
	@Override
	protected ResponseEntity<Object> handleExceptionInternal(
			Exception ex, @Nullable Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {

		if (HttpStatus.INTERNAL_SERVER_ERROR.equals(status)) {
			request.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, ex, WebRequest.SCOPE_REQUEST);
		} 
		ApiError error = new ApiError(status.name(), ex.getLocalizedMessage());
		LOG.info("", ex);
		
		return new ResponseEntity<Object>(error, headers, status);
	}
	
     
    // 400 MethodArgumentTypeMismatchException
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
    	
  	String error = String.format("Invalid value %s must be type of %s", ex.getValue(), ex.getRequiredType().getCanonicalName());
    ApiError apiError =  new ApiError("Bad Request", error);
    
    return new ResponseEntity<ApiError>(apiError, HttpStatus.BAD_REQUEST);
    }
   	 
	 
    // 400 MethodArgumentNotValidException
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {

		// Loop through validation errors and add to errors Array
		List<String> errors = new ArrayList<String>();
		for (FieldError error : ex.getBindingResult().getFieldErrors()) {
			errors.add(error.getField() + ": " + error.getDefaultMessage());
		}
		for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
			errors.add(error.getObjectName() + ": " + error.getDefaultMessage());
		}

		ApiError apiError = new ApiError("Invalid Request", String.join(",", errors));
		 return new ResponseEntity<Object>(apiError, HttpStatus.BAD_REQUEST);
	}

	// 404 ResourceNotFoundException
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handle(ResourceNotFoundException ex, 
                HttpServletRequest request, HttpServletResponse response) {

        ApiError error = new ApiError("Not Found", ex.getLocalizedMessage());
        return new ResponseEntity<ApiError>(error, HttpStatus.NOT_FOUND);
    }	
    
    
    @ExceptionHandler(InvalidSearchFieldException.class)
    public ResponseEntity<ApiError> handleInvalidSearchFieldException(InvalidSearchFieldException ex, 
            HttpServletRequest request, HttpServletResponse response) {

    ApiError error = new ApiError("Bad Request", ex.getLocalizedMessage());
    return new ResponseEntity<ApiError>(error, HttpStatus.BAD_REQUEST);
}	
    
	// 500 Exception
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handle(Exception ex, 
                HttpServletRequest request, HttpServletResponse response) {
    	
        if (ex instanceof NullPointerException) {
        	ApiError error = new ApiError("Bad Request", ex.getLocalizedMessage());
            return new ResponseEntity<ApiError>(error, HttpStatus.BAD_REQUEST);
        }
        
		// Make sure we've enough details in log for 500
		LOG.error("Internal Server Error", ex);
        
        ApiError error = new ApiError("Internal Server Error", null);
        return new ResponseEntity<ApiError>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }	

}
