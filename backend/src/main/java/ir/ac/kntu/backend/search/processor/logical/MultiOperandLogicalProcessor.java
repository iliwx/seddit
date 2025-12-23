package ir.ac.kntu.backend.search.processor.logical;

import ir.ac.kntu.backend.search.ProcessorUtil;
import ir.ac.kntu.backend.search.expression.logical.MultiOperandLogicalExpression;
import ir.ac.kntu.backend.search.processor.IBooleanProcessor;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class MultiOperandLogicalProcessor implements IBooleanProcessor<MultiOperandLogicalExpression> {

	@Override
	public Predicate process(MultiOperandLogicalExpression expression, Root<?> root, CriteriaBuilder builder) {
		final Predicate[] predicates = expression.getOperands().stream()
			.map(expr -> ProcessorUtil.process(expr, root, builder))
			.toArray(Predicate[]::new);

		switch (expression.getOperator()) {
			case And:
				return builder.and(predicates);
			case Or:
				return builder.or(predicates);
			default:
				throw new RuntimeException("Invalid LogicalOperator.EMultiOperand: " + expression.getOperator());
		}
	}
}
