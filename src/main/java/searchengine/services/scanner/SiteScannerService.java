package searchengine.services.scanner;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import searchengine.config.SitesList;
import searchengine.data.dto.common.PageDto;
import searchengine.data.dto.scanner.PageParseResultDto;
import searchengine.data.dto.scanner.ScanTaskDto;
import searchengine.data.model.SiteParameters;
import searchengine.data.model.SiteStatus;
import searchengine.services.common.ExecutorServiceHandler;
import searchengine.services.common.LemmaParserService;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class SiteScannerService {

    private final SiteService siteService;
    private final PageService pageService;
    private final ExecutorServiceHandler executorServiceHandler;
    private final LemmaParserService lemmaParserService;

    private AtomicInteger currentScanningSiteId = new AtomicInteger(0);

    public void unexpectedStop() {
        executorServiceHandler.shutdownAndStopTasks();
        log.info("{} !!!!!!!!!!!!! unexpectedStop SiteScannerService for site: {}", Thread.currentThread().getName(), currentScanningSiteId.intValue());
    }

    @Async
    public void startAllSitesScan(SitesList sitesList) {
        executorServiceHandler.cleanQueue();
        sitesList.getSites().forEach(this::startOneSiteScan);
    }

    @Async
    public void startOnePageScan(ScanTaskDto task) {
        log.info("{} START SiteScannerService for URL: {}", Thread.currentThread().getName(), task.getFullUrl());
        executorServiceHandler.get().submit(() -> call(task));
        executorServiceHandler.cleanQueue();
    }

    private void startOneSiteScan(SiteParameters parameter) {
        if (executorServiceHandler.isNotStopped()) {
            log.info("{} START SiteScannerService for URL: {}", Thread.currentThread().getName(), parameter.getUrl());
            int siteId = siteService.prepareSiteToStartScanning(parameter);
            currentScanningSiteId = new AtomicInteger(siteId);
            executeScanTask(new ScanTaskDto(parameter.getUrl(), "/", siteId));
            siteService.endSiteScanning(siteId, executorServiceHandler.isNotStopped());
            log.info("{} END SiteScannerService for URL: {}", Thread.currentThread().getName(), parameter.getUrl());
        } else {
            log.info("{} MARK SITE AS FILED: {}", Thread.currentThread().getName(), parameter.getUrl());
            siteService
                    .findSiteByUrl(parameter.getUrl())
                    .ifPresent(site -> siteService.endSiteScanning(site.getId(), false));
            ;
        }
    }

    private void executeScanTask(ScanTaskDto scanTaskDto) {
        executorServiceHandler.pushTask(scanTaskDto);
        while (executorServiceHandler.isNotEmptyQueue() || executorServiceHandler.isActive()) {
            ScanTaskDto taskDto = executorServiceHandler.popTask();
            if (taskDto != null) {
                siteService.updateSiteStatus(taskDto.getSiteId(), SiteStatus.INDEXING, "");
                executorServiceHandler.get().submit(() -> call(taskDto));
            }
        }
    }

    private PageParseResultDto call(ScanTaskDto taskDto) {
        if (executorServiceHandler.isNotStopped()) {
            PageParseResultDto resultDto = pageService.scanPage(taskDto);
            String pageText = Jsoup.parseBodyFragment(resultDto.getPage().getPageContent()).text();
            lemmaParserService.parseAndSaveAllLemmas(pageText, taskDto.getSiteId(), resultDto.getPage().getId());
            resultDto.getResultUrls().forEach(executorServiceHandler::pushTask);
            return resultDto;
        } else {
            log.info("{} called task MARK AS FILED: {}", Thread.currentThread().getName(), taskDto.getFullUrl());
            PageDto failedPage = new PageDto(taskDto.getSiteId(), taskDto.getPath(), HttpStatus.BAD_REQUEST.value(), "");
            pageService.save(failedPage);
            return null;
        }
    }


}
