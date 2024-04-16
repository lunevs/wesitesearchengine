package searchengine.services.scanner;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import searchengine.config.SitesList;
import searchengine.data.dto.PageParseResultDto;
import searchengine.data.dto.ScanTaskDto;
import searchengine.data.model.SiteStatus;
import searchengine.services.search.LemmaFinderService;

@Slf4j
@Service
@RequiredArgsConstructor
public class SiteScannerService {

    private final SiteService siteService;
    private final PageService pageService;
    private final ExecutorServiceHandler executorServiceHandler;
    private final LemmaFinderService lemmaFinderService;

    public void unexpectedStop() {
        executorServiceHandler.shutdownAndStopTasks();
        while (!executorServiceHandler.getQueue().isEmpty()) {
            ScanTaskDto taskDto = executorServiceHandler.getQueue().pop();
            if (taskDto != null) {
                pageService.save(taskDto.getSiteId(), taskDto.getPath(), HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
            }
        }
        siteService.markAllSitesAsFailed();
    }

    @Async
    public void start(SitesList sitesList) {
        sitesList.getSites().forEach(parameter -> {
            log.info(Thread.currentThread().getName() + " start SiteScannerService for URL: " + parameter.getUrl());
            int siteId = siteService.prepareSiteToStartScanning(parameter);
            executorServiceHandler.getQueue().push(new ScanTaskDto(parameter.getUrl(), "/", siteId));
        });
        doScan();
    }

    private void doScan() {
        while (!executorServiceHandler.getQueue().isEmpty() || executorServiceHandler.isActive()) {
            ScanTaskDto taskDto = executorServiceHandler.getQueue().pop();
            if (taskDto != null) {
                executorServiceHandler.get().submit(() -> call(taskDto));
            }

//            System.out.printf("******************************************\n");
//            System.out.println(Thread.currentThread().getName());
//            System.out.printf("Main: Active Threads: %d\n", executorServiceHandler.getPool().getActiveThreadCount());
//            System.out.printf("Main: Task Count: %d\n", executorServiceHandler.getPool().getQueuedTaskCount());
//            System.out.printf("Main: getRunningThreadCount: %d\n", executorServiceHandler.getPool().getRunningThreadCount());
//            System.out.printf("Main: getPoolSize: %d\n", executorServiceHandler.getPool().getPoolSize());
//            System.out.printf("******************************************\n");
//            try {
//                TimeUnit.SECONDS.sleep(1);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
        }
        log.info("job is done!");
        siteService.markAllSitesAsIndexed();
    }

    private PageParseResultDto call(ScanTaskDto taskDto) {
        PageParseResultDto resultDto = pageService.parseAndSavePage(taskDto);
        String pageText = Jsoup.parseBodyFragment(resultDto.getPage().getPageContent()).text();
        lemmaFinderService.parseAndSaveAllLemmas(pageText, taskDto.getSiteId(), resultDto.getPage().getId());
        siteService.updateSiteStatus(taskDto.getSiteId(), SiteStatus.INDEXING, "");
        resultDto.getResultUrls().forEach(task -> executorServiceHandler.getQueue().push(task));
        return resultDto;
    }


}
