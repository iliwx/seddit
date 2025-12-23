package ir.ac.kntu.backend.search.processor.comparison;

import ir.ac.kntu.backend.search.ProcessorUtil;
import ir.ac.kntu.backend.search.expression.comparison.RangeValueComparisonExpression;
import ir.ac.kntu.backend.search.processor.IBooleanProcessor;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class RangeValueComparisonProcessor implements IBooleanProcessor<RangeValueComparisonExpression> {

	@Override
	public Predicate process(RangeValueComparisonExpression expression, Root<?> root, CriteriaBuilder builder) {
		final Path path = ProcessorUtil.findPath(root, expression.getProperty());

		switch (expression.getOperator()) {
			case Between:
				return builder.between(path,
					ProcessorUtil.convertSingleValue(expression.getStart(), path),
					ProcessorUtil.convertSingleValue(expression.getEnd(), path));
			default:
				throw new RuntimeException("Invalid ComparisonOperator.ERangeValue: " + expression.getOperator());
		}

	}
}
