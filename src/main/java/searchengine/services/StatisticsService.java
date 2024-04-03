package searchengine.services;

import searchengine.data.dto.StatisticsResponse;


public interface StatisticsService {
    StatisticsResponse getStatistics();

    StatisticsResponse startIndexing();

    StatisticsResponse stopIndexing();
}
