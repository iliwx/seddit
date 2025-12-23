package ir.ac.kntu.backend.search;

import lombok.RequiredArgsConstructor;
import ir.ac.kntu.backend.search.expression.ABooleanExpression;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.*;
import java.util.List;
import java.util.stream.Collectors;

import static ir.ac.kntu.backend.search.SortExpression.EMode.Asc;

@RequiredArgsConstructor
public class SearchSpecification<T> implements Specification<T> {
	private final ABooleanExpression<?> expression;
	private final List<SortExpression> sorts;
	private final boolean distinct;

	// ------------------------------

	public SearchSpecification(ABooleanExpression<?> expression) {
		this(expression, null, false);
	}

	public SearchSpecification(ABooleanExpression<?> expression, List<SortExpression> sorts) {
		this(expression, sorts, false);
	}

	// ------------------------------

	@Override
	public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
		if (sorts != null && !sorts.isEmpty()) {
			final List<Order> orders = sorts.stream()
				.map(sort -> sort.getMode() == Asc ?
					builder.asc(ProcessorUtil.findPath(root, sort.getProperty())) :
					builder.desc(ProcessorUtil.findPath(root, sort.getProperty())))
				.collect(Collectors.toList());
			query.orderBy(orders);
		}

		query.distinct(distinct);

		return expression != null ? ProcessorUtil.process(expression, root, builder) : null;
	}
}