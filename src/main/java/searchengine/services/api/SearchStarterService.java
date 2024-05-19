package searchengine.services.api;

import searchengine.data.dto.search.SearchResponse;

import java.util.Map;

public interface SearchStarterService {

    SearchResponse doSearch(Map<String, String> requestParameters);

}
