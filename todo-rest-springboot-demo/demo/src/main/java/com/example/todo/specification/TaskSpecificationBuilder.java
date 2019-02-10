package com.example.todo.specification;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.jpa.domain.Specification;

import com.example.todo.model.Task;

public class TaskSpecificationBuilder {
    
    private final List<SearchCriteria> params;
 
    public TaskSpecificationBuilder() {
        params = new ArrayList<SearchCriteria>();
    }
 
    public TaskSpecificationBuilder with(String key, String operation, Object value) {
        params.add(new SearchCriteria(key, operation, value));
        return this;
    }
  
    public Specification<Task> build() {
        if (params.size() == 0) {
            return null;
        }
 
        List<Specification<Task>> specs = params.stream()
          .map(TaskSpecification::new)
          .collect(Collectors.toList());
         
        Specification<Task> result = specs.get(0);
 
        for (int i = 1; i < params.size(); i++) {
        	
        	result = Specification.where(result).and(specs.get(i));

//            result = params.get(i)
//              .isOrPredicate()
//                ? Specification.where(result)
//                  .or(specs.get(i))
//                : Specification.where(result)
//                  .and(specs.get(i));
        }       
        return result;
    }
}