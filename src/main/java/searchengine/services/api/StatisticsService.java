package searchengine.services.api;

import searchengine.data.dto.StatisticsResponse;


public interface StatisticsService {
    StatisticsResponse getStatistics();

    StatisticsResponse startIndexing();

    StatisticsResponse stopIndexing();
}
