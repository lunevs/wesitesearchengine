package searchengine.services.api;

import lombok.RequiredArgsConstructor;
import searchengine.data.dto.search.SearchResponse;
import searchengine.services.search.SearchService;

import java.util.Collections;
import java.util.Map;

@RequiredArgsConstructor
public class SearchStarterServiceImpl implements SearchStarterService {

    private static final int DEFAULT_SEARCH_LIMIT = 20;
    private static final int DEFAULT_SEARCH_OFFSET = 0;

    private final SearchService searchService;

    @Override
    public SearchResponse doSearch(Map<String, String> requestParameters) {
        if (requestParameters.get("query").isBlank()) {
            return new SearchResponse(true, 0, Collections.emptyList());
        }
        String query = requestParameters.get("query");
        String siteUrl = requestParameters.get("site");
        int offset = requestParameters.get("offset") == null ? DEFAULT_SEARCH_OFFSET : Integer.parseInt(requestParameters.get("offset"));
        int limit = requestParameters.get("limit") == null ? DEFAULT_SEARCH_LIMIT : Integer.parseInt(requestParameters.get("limit"));
        return searchService.searchStart(query, siteUrl, offset, limit);
    }


}
