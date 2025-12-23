package ir.ac.kntu.backend.search.expression.comparison;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ir.ac.kntu.backend.search.expression.ABooleanExpression;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
public class MultiValueComparisonExpression extends ABooleanExpression<ComparisonOperator.EMultiValue> {
	@NotBlank
	private String property;

	@NotEmpty
	private List<String> values;

	public MultiValueComparisonExpression(ComparisonOperator.EMultiValue operator, String property, String... values) {
		setOperator(operator);
		this.property = property;
		this.values = Arrays.asList(values);
	}

	@Override
	public void validate() {
		super.validate();

		if (isEmpty(property)) {
			throw new RuntimeException("Property Required: " + this);
		}

		if (values == null || values.isEmpty()) {
			throw new RuntimeException("Values Required: " + this);
		}
	}
}
