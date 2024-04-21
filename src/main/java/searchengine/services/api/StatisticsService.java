package searchengine.services.api;

import searchengine.data.dto.SearchResponse;
import searchengine.data.dto.StatisticsResponse;

import java.util.Map;


public interface StatisticsService {
    StatisticsResponse getStatistics();

    StatisticsResponse startIndexing();

    StatisticsResponse stopIndexing();

    SearchResponse doSearch(Map<String, String> requestParameters);
}
