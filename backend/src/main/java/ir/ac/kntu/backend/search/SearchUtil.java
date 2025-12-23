package ir.ac.kntu.backend.search;

import ir.ac.kntu.backend.DTO.SearchDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.function.Function;
import java.util.stream.Collectors;

public class SearchUtil {

	public static <E, D> SearchDTO.SearchRs<D> search(
		JpaSpecificationExecutor<E> repository,
		SearchDTO.SearchRq searchRq,
		Function<E, D> entityToDTO) {
		return search(repository, searchRq, true, entityToDTO);
	}

	public static <E, D> SearchDTO.SearchRs<D> search(
		JpaSpecificationExecutor<E> repository,
		SearchDTO.SearchRq searchRq,
		Boolean calcTotal,
		Function<E, D> entityToDTO) {

		final SearchSpecification<E> specification = new SearchSpecification<>(searchRq.getFilter(), searchRq.getSorts(), searchRq.getDistinct());
		final Page<E> page = repository.findAll(specification, new PageRequest(searchRq.getStartIndex(), searchRq.getCount(), calcTotal));

		return new SearchDTO.SearchRs<>(
			page.getContent().stream().map(entityToDTO).collect(Collectors.toList()),
			page.getTotalElements()
		);
	}

}
