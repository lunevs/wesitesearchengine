package searchengine.services.search;

import searchengine.data.dto.search.SearchResponse;

public interface SearchService {

    SearchResponse searchStart(String query, String siteUrl, int offset, int limit);


}
