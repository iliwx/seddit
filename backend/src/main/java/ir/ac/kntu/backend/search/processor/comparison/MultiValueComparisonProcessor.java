package ir.ac.kntu.backend.search.processor.comparison;

import ir.ac.kntu.backend.search.expression.comparison.MultiValueComparisonExpression;
import ir.ac.kntu.backend.search.processor.IBooleanProcessor;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.List;

import static ir.ac.kntu.backend.search.ProcessorUtil.convertListValue;
import static ir.ac.kntu.backend.search.ProcessorUtil.findPath;

public class MultiValueComparisonProcessor implements IBooleanProcessor<MultiValueComparisonExpression> {

	@Override
	public Predicate process(MultiValueComparisonExpression expression, Root<?> root, CriteriaBuilder builder) {
		final Path path = findPath(root, expression.getProperty());
		final List values = convertListValue(expression.getValues(), path);

		switch (expression.getOperator()) {
			case In:
				return path.in(values);
			case NotIn:
				return builder.not(path.in(values));
			default:
				throw new RuntimeException("Invalid ComparisonOperator.EMultiValue: " + expression.getOperator());
		}
	}
}
