package searchengine.services.search;

import searchengine.data.dto.search.SearchResultsDto;

import java.util.List;

public interface SearchResultsService {

    void findPages();
    void enrichData();
    List<SearchResultsDto> getSearchResults();

}
