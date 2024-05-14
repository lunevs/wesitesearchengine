package searchengine.services.api;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import searchengine.data.dto.search.SearchResponse;
import searchengine.services.search.SearchService;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class SearchStarterService {

    private static final int DEFAULT_SEARCH_LIMIT = 20;
    private static final int DEFAULT_SEARCH_OFFSET = 0;

    private final SearchService searchService;


    public SearchResponse doSearch(Map<String, String> requestParameters) {
        if (requestParameters.get("query").isBlank()) {
            throw new IllegalArgumentException("Задан пустой поисковый запрос");
        }
        String query = requestParameters.get("query");
        String siteUrl = requestParameters.get("site");
        int offset = requestParameters.get("offset") == null ? DEFAULT_SEARCH_OFFSET : Integer.parseInt(requestParameters.get("offset"));
        int limit = requestParameters.get("limit") == null ? DEFAULT_SEARCH_LIMIT : Integer.parseInt(requestParameters.get("limit"));
        return searchService.searchStart(query, siteUrl, offset, limit);
    }


}
