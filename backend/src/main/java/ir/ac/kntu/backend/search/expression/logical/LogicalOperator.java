package ir.ac.kntu.backend.search.expression.logical;

public abstract class LogicalOperator {
	public enum EMultiOperand {
		And,
		Or
	}

	public enum ESingleOperand {
		Not
	}
}
