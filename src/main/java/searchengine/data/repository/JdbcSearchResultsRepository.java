package searchengine.data.repository;

import searchengine.data.dto.search.SearchResultsDto;

import java.util.List;
import java.util.Set;

public interface JdbcSearchResultsRepository {

    List<SearchResultsDto> findAll(Set<Integer> lemmasIds, Set<Integer> pagesIds);

}
