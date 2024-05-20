package searchengine.services.search;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import searchengine.data.dto.search.SearchResponseItem;
import searchengine.data.dto.search.SearchResultsDto;
import searchengine.data.dto.search.SearchResponse;

import java.util.Comparator;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class SearchServiceImpl implements SearchService {

    private final SearchQueryHolder searchQueryHolder;
    private final LemmasHolder lemmasHolder;
    private final SearchResultsService searchResultsService;

    @Override
    public SearchResponse searchStart(String query, String siteUrl, int offset, int limit) {
        log.info("запросу на поиск строки: {} на сайте: {}", query, siteUrl);
        searchQueryHolder.init(query, siteUrl);
        lemmasHolder.load();
        searchResultsService.findPages();
        searchResultsService.enrichData();
        List<SearchResultsDto> searchResults = searchResultsService.getSearchResults();
        log.info("по запросу: {} найдено {} результатов", query, searchResults.size());
        if (searchResults.isEmpty()) {
            throw new IllegalArgumentException("Указанный запрос не найден");
        }
        return SearchResponse.of(
                formatResults(searchResults, limit, offset),
                searchResults.size());
    }

    private List<SearchResponseItem> formatResults(List<SearchResultsDto> searchResults, int limit, int offset) {
        return searchResults.stream()
                .sorted(Comparator.comparingInt(SearchResultsDto::getAbsFrequency).reversed())
                .skip(offset)
                .limit(limit)
                .map(SearchResponseItem::of)
                .toList();
    }


}
