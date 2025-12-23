package ir.ac.kntu.backend.search.expression;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ir.ac.kntu.backend.search.expression.comparison.MultiValueComparisonExpression;
import ir.ac.kntu.backend.search.expression.comparison.NoValueComparisonExpression;
import ir.ac.kntu.backend.search.expression.comparison.RangeValueComparisonExpression;
import ir.ac.kntu.backend.search.expression.comparison.SingleValueComparisonExpression;
import ir.ac.kntu.backend.search.expression.logical.MultiOperandLogicalExpression;

import jakarta.validation.constraints.NotNull;

@JsonTypeInfo(
	use = JsonTypeInfo.Id.NAME,
	property = "operator",
	include = JsonTypeInfo.As.EXISTING_PROPERTY,
	visible = true,
	defaultImpl = SingleValueComparisonExpression.class)
@JsonSubTypes({
	@JsonSubTypes.Type(names = {"And", "Or"}, value = MultiOperandLogicalExpression.class),
	@JsonSubTypes.Type(names = {"Empty", "NotEmpty"}, value = NoValueComparisonExpression.class),
	@JsonSubTypes.Type(names = {"Between"}, value = RangeValueComparisonExpression.class),
	@JsonSubTypes.Type(names = {"In", "NotIn"}, value = MultiValueComparisonExpression.class)
})
@Getter
@Setter
@ToString
public abstract class ABooleanExpression<T> {
	@NotNull
	private T operator;

	public void validate() {
		if (operator == null) {
			throw new RuntimeException("Operator Required: " + this);
		}
	}

	protected boolean isEmpty(String value) {
		return value == null || value.trim().isEmpty();
	}
}
