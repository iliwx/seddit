package ir.ac.kntu.backend.search.expression.logical;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ir.ac.kntu.backend.search.expression.ABooleanExpression;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
public class SingleOperandLogicalExpression extends ABooleanExpression<LogicalOperator.ESingleOperand> {
	@Valid
	@NotNull
	private ABooleanExpression<?> operand;

	public SingleOperandLogicalExpression(LogicalOperator.ESingleOperand operator, ABooleanExpression<?> operand) {
		setOperator(operator);
		this.operand = operand;
	}

	@Override
	public void validate() {
		super.validate();

		if (operand == null) {
			throw new RuntimeException("Operand Required: " + this);
		}
	}
}
