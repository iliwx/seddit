package ir.ac.kntu.backend.search.expression.comparison;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ir.ac.kntu.backend.search.expression.ABooleanExpression;

import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
public class SingleValueComparisonExpression extends ABooleanExpression<ComparisonOperator.ESingleValue> {
	@NotBlank
	private String property;

	@NotBlank
	private String value;

	public SingleValueComparisonExpression(ComparisonOperator.ESingleValue operator, String property, String value) {
		setOperator(operator);
		this.property = property;
		this.value = value;
	}

	@Override
	public void validate() {
		super.validate();

		if (isEmpty(property)) {
			throw new RuntimeException("Property Required: " + this);
		}

		if (isEmpty(value)) {
			throw new RuntimeException("Value Required: " + this);
		}
	}
}
