package searchengine.services.search;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import searchengine.data.dto.search.SearchResponseItem;
import searchengine.data.dto.search.SearchResultsDto;
import searchengine.data.dto.search.SearchResponse;

import java.util.Comparator;

import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class SearchService {

    private final SearchQueryHolder searchQueryHolder;
    private final LemmaFrequencyCalculator lemmaFrequencyCalculator;
    private final SearchResultsService searchResultsService;

    public SearchResponse searchStart(String query, String siteUrl, int offset, int limit) {
        searchQueryHolder.init(query, siteUrl);
        lemmaFrequencyCalculator.calc();
        List<SearchResultsDto> searchResults = searchResultsService
                .findPages()
                .buildResults()
                .enrichData()
                .getSearchResults();
        log.info("for query: {} found {} results", query, searchResults.size());
        if (searchResults.isEmpty()) {
            throw new IllegalArgumentException("Указанный запрос не найден");
        }
        return SearchResponse.of(
                prepareResults(searchResults, limit, offset),
                searchResults.size());
    }

    private List<SearchResponseItem> prepareResults(List<SearchResultsDto> searchResults, int limit, int offset) {
        return searchResults.stream()
                .sorted(Comparator.comparingInt(SearchResultsDto::getAbsFrequency).reversed())
                .skip(offset)
                .limit(limit)
                .map(SearchResponseItem::of)
                .toList();
    }


}
