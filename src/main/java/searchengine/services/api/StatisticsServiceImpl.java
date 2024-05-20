package searchengine.services.api;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import searchengine.data.dto.api.DetailedStatisticsItem;
import searchengine.data.dto.api.StatisticsData;
import searchengine.data.dto.api.StatisticsResponse;
import searchengine.data.dto.api.TotalStatistics;
import searchengine.data.model.Site;
import searchengine.data.repository.JdbcRepository;
import searchengine.services.scanner.SiteService;

import java.util.List;

@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final SiteService siteService;
    private final JdbcRepository jdbcRepository;

    @Override
    public StatisticsResponse getStatistics() {
        List<Site> siteList = siteService.findAll();

        int totalPages = 0;
        int totalLemmas = 0;
        List<DetailedStatisticsItem> detailed = jdbcRepository.getDetailedSitesStatistics();
        for (DetailedStatisticsItem item : detailed) {
            totalPages += item.getPages();
            totalLemmas += item.getLemmas();
        }
        TotalStatistics total = TotalStatistics.of(siteList.size(), totalPages, totalLemmas, true);
        StatisticsData data = StatisticsData.of(total, detailed);
        return new StatisticsResponse(true, data);
    }
}
