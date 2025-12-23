package ir.ac.kntu.backend.search;

import lombok.Getter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PageRequest implements Pageable {
	private final Integer startIndex;
	private final Integer count;
	@Getter
    private final Boolean calcTotal;

	public PageRequest(Integer startIndex, Integer count) {
		this(startIndex, count, true);
	}

	public PageRequest(Integer startIndex, Integer count, Boolean calcTotal) {
		if (startIndex == null || startIndex < 0) {
			throw new RuntimeException("Invalid startIndex: " + startIndex);
		}
		if (count == null || count < 1) {
			throw new RuntimeException("Invalid count: " + count);
		}

		this.startIndex = startIndex;
		this.count = count;
		this.calcTotal = calcTotal;
	}

    @Override
	public int getPageNumber() {
		return 0;
	}

	@Override
	public int getPageSize() {
		return count;
	}

	@Override
	public long getOffset() {
		return startIndex;
	}

	@Override
	public Sort getSort() {
		return Sort.unsorted();
	}

	@Override
	public Pageable next() {
		throw new RuntimeException("NA");
	}

	@Override
	public Pageable previousOrFirst() {
		throw new RuntimeException("NA");
	}

	@Override
	public Pageable first() {
		throw new RuntimeException("NA");
	}

	@Override
	public Pageable withPage(int pageNumber) {
		throw new RuntimeException("NA");
	}

	@Override
	public boolean hasPrevious() {
		throw new RuntimeException("NA");
	}
}
