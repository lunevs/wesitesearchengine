package searchengine.services.scanner;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;

@Service
@RequiredArgsConstructor
public class ExecutorServiceHandler {

    private final TasksQueueService tasksQueueService;

    private ExecutorService executorService;


    @PostConstruct
    public void init() {
        executorService = Executors.newWorkStealingPool(8);
    }

    @PreDestroy
    public void clean() {
        executorService.shutdown();
    }

    public ExecutorService get() {
        if (executorService == null) {
            init();
        }
        return executorService;
    }

    public ForkJoinPool getPool() {
        return (ForkJoinPool) this.get();
    }

    public boolean isActive() {
        return getPool().getActiveThreadCount() > 0;
    }

    public void shutdownAndStopTasks() {
        executorService.shutdownNow();
    }

    public TasksQueueService getQueue() {
        return tasksQueueService;
    }
}
