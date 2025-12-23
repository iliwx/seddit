package ir.ac.kntu.backend.search.processor;

import ir.ac.kntu.backend.search.expression.ABooleanExpression;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public interface IBooleanProcessor<B extends ABooleanExpression<?>> {
	Predicate process(B expression, Root<?> root, CriteriaBuilder builder);
}
