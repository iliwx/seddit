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
public class RangeValueComparisonExpression extends ABooleanExpression<ComparisonOperator.ERangeValue> {
	@NotBlank
	private String property;

	@NotBlank
	private String start;

	@NotBlank
	private String end;

	public RangeValueComparisonExpression(ComparisonOperator.ERangeValue operator, String property, String start, String end) {
		setOperator(operator);
		this.property = property;
		this.start = start;
		this.end = end;
	}

	@Override
	public void validate() {
		super.validate();

		if (isEmpty(property)) {
			throw new RuntimeException("Property Required: " + this);
		}

		if (isEmpty(start) || isEmpty(end)) {
			throw new RuntimeException("Start or End Required: " + this);
		}
	}
}
