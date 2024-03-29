package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import searchengine.data.dto.PageParseResultDto;
import searchengine.data.dto.ScanTaskDto;
import searchengine.data.repository.JdbcPageRepository;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

@Service
@RequiredArgsConstructor
public class PageScannerService {

    private final ExecutorService siteScannerExecutorService;
    private final JdbcPageRepository pageRepository;

    private final ConcurrentLinkedQueue<PageScannerTask> tasks = new ConcurrentLinkedQueue<>();
    private final Set<String> visitedUrls = ConcurrentHashMap.newKeySet();


    public void start(ScanTaskDto scanTaskDto) throws InterruptedException {
        int siteId = scanTaskDto.getSiteId();
        tasks.add(new PageScannerTask(scanTaskDto));
        pageRepository.deleteAllBySiteId(siteId);

        while (!tasks.isEmpty()) {
            List<Future<PageParseResultDto>> futureResults = siteScannerExecutorService.invokeAll(tasks);
            tasks.forEach(task -> visitedUrls.add(task.getInputUrl().getFullUrl()));
            tasks.clear();
            futureResults.forEach(this::saveChildrenUrls);
        }
        System.out.println("visited urls: " + visitedUrls.size());
        visitedUrls.clear();
    }

    public void stop() {
        List<Runnable> queueTasks = siteScannerExecutorService.shutdownNow();
        System.out.println("Receive stop command. Result size: " + queueTasks.size());
    }

    private void saveChildrenUrls(Future<PageParseResultDto> futureResult) {
        try {
            PageParseResultDto results = futureResult.get();
            pageRepository.saveAll(List.of(results.getPage()));
            results.getResultUrls().forEach(url -> {
                if (!visitedUrls.contains(url.getFullUrl())) {
                    tasks.add(new PageScannerTask(url));
                }
            });
        } catch (InterruptedException | ExecutionException e) {
            System.out.println(e.getMessage());
        }
    }

}
