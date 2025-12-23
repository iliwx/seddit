package ir.ac.kntu.backend.search.processor.logical;

import ir.ac.kntu.backend.search.ProcessorUtil;
import ir.ac.kntu.backend.search.expression.logical.SingleOperandLogicalExpression;
import ir.ac.kntu.backend.search.processor.IBooleanProcessor;

import ir.ac.kntu.backend.search.ProcessorUtil;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class SingleOperandLogicalProcessor implements IBooleanProcessor<SingleOperandLogicalExpression> {

	@Override
	public Predicate process(SingleOperandLogicalExpression expression, Root<?> root, CriteriaBuilder builder) {
		switch (expression.getOperator()) {
			case Not:
				return builder.not(ProcessorUtil.process(expression.getOperand(), root, builder));
			default:
				throw new RuntimeException("Invalid LogicalOperator.ESingleOperand: " + expression.getOperator());
		}
	}
}
