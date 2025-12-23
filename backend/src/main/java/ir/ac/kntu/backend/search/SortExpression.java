package ir.ac.kntu.backend.search;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class SortExpression {
	public enum EMode {
		Asc, Desc
	}

	@NotBlank
	private String property;

	@NotNull
	private EMode mode = EMode.Asc;

	// ------------------------------

	public SortExpression(String property) {
		this.property = property;
	}
}
