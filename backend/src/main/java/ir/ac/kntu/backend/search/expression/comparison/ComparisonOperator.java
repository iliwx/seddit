package ir.ac.kntu.backend.search.expression.comparison;

public abstract class ComparisonOperator {
	public enum EMultiValue {
		In,
		NotIn
	}

	public enum ERangeValue {
		Between
	}

	public enum ESingleValue {
		Equal,
		NotEqual,
		LessThan,
		LessThanEqual,
		GreaterThan,
		GreaterThanEqual,
		StartWith,
		EndWith,
		Contain
	}

	public enum ENoValue {
		Empty,
		NotEmpty
	}
}
