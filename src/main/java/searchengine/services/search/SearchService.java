package searchengine.services.search;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import searchengine.data.dto.SearchResponseItem;
import searchengine.data.dto.SearchResultsDto;
import searchengine.data.dto.SearchResponse;

import java.util.Comparator;

import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class SearchService {

    private final SearchQueryHolder searchQueryHolder;
    private final SearchLemmasHolder searchLemmasHolder;
    private final SearchResultsService searchResultsService;


    public SearchResponse searchStart(String query, String siteUrl, Integer offset, Integer limit) {
        searchQueryHolder.init(query);
        searchLemmasHolder.init(searchQueryHolder.getQueryLemmas());
        if (searchLemmasHolder.getSearchLemmasList().isEmpty()) {
            return SearchResponse.emptyResponse();
        }
        return SearchResponse.of(processSearch(limit));
    }

    private List<SearchResponseItem> processSearch(Integer limit) {
        List<SearchResultsDto> searchResults = searchResultsService
                .findPages()
                .buildResults()
                .enrichData()
                .getSearchResults();
        return prepareResults(searchResults, limit);
    }

    private List<SearchResponseItem> prepareResults(List<SearchResultsDto> searchResults, Integer limit) {
        return searchResults.stream()
                .sorted(Comparator.comparingInt(SearchResultsDto::getAbsFrequency).reversed())
                .limit(limit)
                .map(SearchResponseItem::of)
                .toList();
    }


}
