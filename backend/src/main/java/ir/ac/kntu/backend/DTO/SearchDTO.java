package ir.ac.kntu.backend.DTO;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import ir.ac.kntu.backend.search.SortExpression;
import ir.ac.kntu.backend.search.expression.ABooleanExpression;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public abstract class SearchDTO {

	@Getter
	@Setter
	@Accessors(chain = true)
	public static class SearchRq {
		@NotNull
		@Min(0)
		private Integer startIndex;

		@NotNull
		@Min(1)
		private Integer count;

		@NotNull
		private Boolean distinct = Boolean.FALSE;

		@Valid
		private ABooleanExpression<?> filter;

		@Valid
		private List<SortExpression> sorts;
	}

	@Getter
	@RequiredArgsConstructor
	public static class SearchRs<D> {
		private final List<D> result;
		private final Long totalCount;
	}
}
