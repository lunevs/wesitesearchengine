package searchengine.data.repository;

import searchengine.data.dto.api.DetailedStatisticsItem;
import searchengine.data.dto.common.PageDto;

import java.util.List;
import java.util.Set;

public interface JdbcRepository {

    List<DetailedStatisticsItem> getDetailedSitesStatistics();
    List<PageDto> getPagesContainsAllLemmas(Set<Integer> lemmaIds);

}
