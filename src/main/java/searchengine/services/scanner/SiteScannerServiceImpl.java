package searchengine.services.scanner;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import searchengine.config.SitesList;
import searchengine.data.dto.scanner.ScanTaskDto;
import searchengine.data.model.SiteParameters;
import searchengine.data.model.SiteStatus;
import searchengine.services.common.ExecutorServiceHandler;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RequiredArgsConstructor
public class SiteScannerServiceImpl implements SiteScannerService {

    private final SiteService siteService;
    private final ExecutorServiceHandler executorServiceHandler;
    private final PageParser pageParser;

    private AtomicInteger currentScanningSiteId = new AtomicInteger(0);

    @Override
    public void unexpectedStop() {
        log.warn("{} пользователь остановил сканирование. В процессе сканирование сайта: {}", Thread.currentThread().getName(), currentScanningSiteId.intValue());
        executorServiceHandler.shutdownAndStopTasks();
    }

    @Override
    @Async
    public void startAllSitesScan(SitesList sitesList) {
        log.info("{} пользователь запустил сканирование {} сайтов", Thread.currentThread().getName(), sitesList.getSites().size());
        executorServiceHandler.cleanQueue();
        sitesList.getSites().forEach(this::startOneSiteScan);
    }

    @Override
    @Async
    public void startOnePageScan(ScanTaskDto task) {
        log.info("{} пользователь запустил сканирование страницы: {}", Thread.currentThread().getName(), task.getFullUrl());
        executorServiceHandler.get().submit(() -> pageParser.call(task));
        executorServiceHandler.cleanQueue();
    }

    private void startOneSiteScan(SiteParameters parameter) {
        if (executorServiceHandler.isNotStopped()) {
            int siteId = siteService.prepareSiteToStartScanning(parameter);
            currentScanningSiteId = new AtomicInteger(siteId);
            executeScanTask(new ScanTaskDto(parameter.getUrl(), "/", siteId));
            siteService.endSiteScanning(siteId, executorServiceHandler.isNotStopped());
            log.info("{} завершено сканирование узла: {}", Thread.currentThread().getName(), parameter.getUrl());
        } else {
            log.warn("{} сканирование сайта {} отменено пользователем", Thread.currentThread().getName(), parameter.getUrl());
            siteService
                    .findSiteByUrl(parameter.getUrl())
                    .ifPresent(site -> siteService.endSiteScanning(site.getId(), false));
        }
    }

    private void executeScanTask(ScanTaskDto scanTaskDto) {
        executorServiceHandler.pushTask(scanTaskDto);
        while (executorServiceHandler.isNotEmptyQueue() || executorServiceHandler.isActive()) {
            ScanTaskDto taskDto = executorServiceHandler.popTask();
            if (taskDto != null) {
                siteService.updateSiteStatus(taskDto.getSiteId(), SiteStatus.INDEXING, "");
                executorServiceHandler.get().submit(() -> pageParser.call(taskDto));
            }
        }
    }

}
