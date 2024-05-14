package searchengine.data.repository;

import searchengine.data.dto.api.DetailedStatisticsItem;

import java.util.List;

public interface JdbcRepository {

    List<DetailedStatisticsItem> getDetailedSitesStatistics();

}
