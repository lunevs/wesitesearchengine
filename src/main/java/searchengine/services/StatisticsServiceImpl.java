package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import searchengine.data.model.SiteParameters;
import searchengine.config.SitesList;
import searchengine.data.dto.DetailedStatisticsItem;
import searchengine.data.dto.StatisticsData;
import searchengine.data.dto.StatisticsResponse;
import searchengine.data.dto.TotalStatistics;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final Random random = new Random();
    private final SitesList sites;
    private final SiteScannerService scannerService;

    @Override
    public StatisticsResponse startIndexing() {

        /*
         удалять все имеющиеся данные по этому сайту (записи из таблиц site и page);
         создавать в таблице site новую запись со статусом INDEXING;
         обходить все страницы, начиная с главной, добавлять их адреса, статусы и содержимое в базу данных в таблицу page;
         в процессе обхода постоянно обновлять дату и время в поле status_time таблицы site на текущее;
         по завершении обхода изменять статус (поле status) на INDEXED;
         если произошла ошибка и обход завершить не удалось, изменять статус на FAILED и вносить в поле last_error понятную информацию о произошедшей ошибке.
         */
        scannerService.start(sites);

        StatisticsResponse response = new StatisticsResponse();
        response.setResult(true);
        return response;
    }

    @Override
    public StatisticsResponse stopIndexing() {
//        siteScannerExecutorService.execute(scannerService::stop);

        StatisticsResponse response = new StatisticsResponse();
        response.setResult(true);
        return response;
    }

    @Override
    public StatisticsResponse getStatistics() {
        String[] statuses = { "INDEXED", "FAILED", "INDEXING" };
        String[] errors = {
                "Ошибка индексации: главная страница сайта не доступна",
                "Ошибка индексации: сайт не доступен",
                ""
        };

        TotalStatistics total = new TotalStatistics();
        total.setSites(sites.getSites().size());
        total.setIndexing(true);

        List<DetailedStatisticsItem> detailed = new ArrayList<>();
        List<SiteParameters> sitesList = sites.getSites();
        for(int i = 0; i < sitesList.size(); i++) {
            SiteParameters siteParameters = sitesList.get(i);
            DetailedStatisticsItem item = new DetailedStatisticsItem();
            item.setName(siteParameters.getName());
            item.setUrl(siteParameters.getUrl());
            int pages = random.nextInt(1_000);
            int lemmas = pages * random.nextInt(1_000);
            item.setPages(pages);
            item.setLemmas(lemmas);
            item.setStatus(statuses[i % 3]);
            item.setError(errors[i % 3]);
            item.setStatusTime(System.currentTimeMillis() -
                    (random.nextInt(10_000)));
            total.setPages(total.getPages() + pages);
            total.setLemmas(total.getLemmas() + lemmas);
            detailed.add(item);
        }

        StatisticsResponse response = new StatisticsResponse();
        StatisticsData data = new StatisticsData();
        data.setTotal(total);
        data.setDetailed(detailed);
        response.setStatistics(data);
        response.setResult(true);
        return response;
    }
}
