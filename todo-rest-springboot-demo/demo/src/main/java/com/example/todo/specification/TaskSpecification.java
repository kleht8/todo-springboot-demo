package com.example.todo.specification;

import org.springframework.data.jpa.domain.Specification;

import com.example.todo.model.Task;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class TaskSpecification implements Specification<Task> {
	 
    /**
	 * 
	 */
	private static final long serialVersionUID = -5595261296988365312L;
	private SearchCriteria criteria;
     
    public TaskSpecification(SearchCriteria criteria) {
		super();
		this.criteria = criteria;
	}

	@Override
    public Predicate toPredicate
      (Root<Task> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
  
        if (criteria.getOperation().equalsIgnoreCase(">")) {
            return builder.greaterThan( 
              root.<String> get(criteria.getKey()), criteria.getValue().toString());
        } 
        else if (criteria.getOperation().equalsIgnoreCase("<")) {
            return builder.lessThan(
              root.<String> get(criteria.getKey()), criteria.getValue().toString());
        } 
        else if (criteria.getOperation().equalsIgnoreCase(":")) {
            if (root.get(criteria.getKey()).getJavaType() == String.class) {
                return builder.like(
                  root.<String>get(criteria.getKey()), "%" + criteria.getValue() + "%");
            } else if (root.get(criteria.getKey()).getJavaType() == Boolean.class) {
            	
            	 return builder.equal(root.get(criteria.getKey()),  Boolean.parseBoolean(criteria.getValue().toString()));
            }
                return builder.equal(root.get(criteria.getKey()), criteria.getValue());
            }
        return null;
    }
}