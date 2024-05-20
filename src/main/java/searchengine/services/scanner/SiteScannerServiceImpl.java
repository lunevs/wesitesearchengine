package searchengine.services.scanner;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import searchengine.config.SitesList;
import searchengine.data.dto.scanner.ScanTaskDto;
import searchengine.data.model.Site;
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
    public void scanAllPages(SitesList sitesList) {
        log.info("{} пользователь запустил сканирование {} сайтов", Thread.currentThread().getName(), sitesList.getSites().size());
        executorServiceHandler.cleanQueue();
        sitesList.getSites().forEach(this::scanItem);
    }

    @Override
    @Async
    public void scanOnePage(ScanTaskDto task) {
        log.info("{} пользователь запустил сканирование страницы: {}", Thread.currentThread().getName(), task.getFullUrl());
        executorServiceHandler.get().submit(() -> pageParser.call(task));
        executorServiceHandler.cleanQueue();
    }

    private void scanItem(SiteParameters parameter) {
        Site site = siteService
                .findSiteByUrl(parameter.getUrl())
                .orElseGet(() -> siteService.create(parameter.getUrl(), parameter.getName()));
        if (executorServiceHandler.isNotStopped()) {
            siteService.deleteAllSiteDependencies(site.getId());
            currentScanningSiteId = new AtomicInteger(site.getId());
            executeScanTask(new ScanTaskDto(parameter.getUrl(), "/", site.getId()));
            log.info("{} завершено сканирование сайта: {}", Thread.currentThread().getName(), parameter.getUrl());
        } else {
            log.warn("{} сканирование сайта {} отменено пользователем", Thread.currentThread().getName(), parameter.getUrl());
        }
        updateSiteStatusAfterScanning(site.getId(), executorServiceHandler.isNotStopped());
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

    private void updateSiteStatusAfterScanning(int siteId, boolean isNotStopped) {
        siteService.updateSiteStatus(
                siteId,
                isNotStopped ? SiteStatus.INDEXED : SiteStatus.FAILED,
                isNotStopped ? "" : "сканирование прервано пользователем");
    }

}
