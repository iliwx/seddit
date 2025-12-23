package ir.ac.kntu.backend.search.processor.comparison;

import ir.ac.kntu.backend.search.ProcessorUtil;
import ir.ac.kntu.backend.search.expression.comparison.NoValueComparisonExpression;
import ir.ac.kntu.backend.search.processor.IBooleanProcessor;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import static ir.ac.kntu.backend.search.ProcessorUtil.findPath;

public class NoValueComparisonProcessor implements IBooleanProcessor<NoValueComparisonExpression> {

	@Override
	public Predicate process(NoValueComparisonExpression expression, Root<?> root, CriteriaBuilder builder) {
		final Path path = findPath(root, expression.getProperty());
		switch (expression.getOperator()) {
			case Empty:
				return path.isNull();
			case NotEmpty:
				return path.isNotNull();
			default:
				throw new RuntimeException("Invalid ComparisonOperator.ENoValue: " + expression.getOperator());
		}
	}
}
