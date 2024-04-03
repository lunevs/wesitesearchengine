package searchengine.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import searchengine.config.SitesList;
import searchengine.data.dto.PageDto;
import searchengine.data.dto.PageParseResultDto;
import searchengine.data.dto.ScanTaskDto;
import searchengine.data.model.SiteStatus;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;

@Slf4j
@Service
@RequiredArgsConstructor
public class SiteScannerService {

    private final SiteService siteService;
    private final TasksQueueService tasksQueueService;
    private final ExecutorService siteScannerExecutorService;


    public void start(SitesList sitesList) {

        sitesList.getSites().forEach(parameter -> {
            log.trace(Thread.currentThread().getName() + " start SiteScannerService for " + parameter.getUrl());

            int siteId = siteService.getSiteByUrlOrCreate(parameter.getUrl(), parameter.getName());
            siteService.updateSiteStatus(siteId, SiteStatus.INDEXING, null);
            tasksQueueService.push(new ScanTaskDto(parameter.getUrl(), "/", siteId));
        });
        siteScannerExecutorService.submit(this::doScan);
    }

    public void doScan() {
        ForkJoinPool pool = (ForkJoinPool) siteScannerExecutorService;

        while (!tasksQueueService.isEmpty() || pool.getActiveThreadCount() > 0) {
            ScanTaskDto taskDto = tasksQueueService.pop();
            if (taskDto != null) {
                siteScannerExecutorService.submit(() -> call(taskDto));
            }
        }
    }

    public PageParseResultDto call(ScanTaskDto taskDto) {

        log.trace(Thread.currentThread().getName() + " call parse page: " + taskDto.getFullUrl());

        Connection.Response response = null;
        try {
            response = Jsoup.connect(taskDto.getFullUrl()).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        PageParseResultDto resultDto = new PageParseResultDto();
        PageDto pageDto = new PageDto()
                .setSiteId(taskDto.getSiteId())
                .setPagePath(taskDto.getPath())
                .setResponseCode(response.statusCode());

        try {
            Document doc = response.parse();
            pageDto.setPageContent(doc.body().html());
            resultDto.setResultUrls(parseNextUrls(doc.select("a[href]"), taskDto));
            resultDto.getResultUrls().forEach(url -> {
                log.trace(Thread.currentThread().getName() + " add url to tasks list: " + url.getFullUrl());
                tasksQueueService.push(url);
            });
            Thread.sleep(200);
        } catch (IOException | InterruptedException e) {
            System.out.println(e.getMessage());
        }
        resultDto.setPage(pageDto);
        return resultDto;
        //TODO save page to DB
    }

    private Set<ScanTaskDto> parseNextUrls(Elements elements, ScanTaskDto taskDto) {
        Set<ScanTaskDto> nextUrls = new HashSet<>();
        for (Element element : elements) {
            String currentPath = element.attr("href");
            if (currentPath.startsWith(taskDto.getUrl())) {
                currentPath = currentPath.substring(taskDto.getUrl().length());
            }
            if (currentPath.startsWith(taskDto.getPath()) && !currentPath.equals(taskDto.getPath())) {
                nextUrls.add(new ScanTaskDto(taskDto.getUrl(), currentPath, taskDto.getSiteId()));
            }
        }
        return nextUrls;
    }
}
