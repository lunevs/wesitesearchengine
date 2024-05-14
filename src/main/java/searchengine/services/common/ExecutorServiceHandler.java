package searchengine.services.common;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import searchengine.data.dto.scanner.ScanTaskDto;
import searchengine.services.scanner.TasksQueueService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
public class ExecutorServiceHandler {

    private final TasksQueueService tasksQueueService;

    private final ExecutorService executorService = Executors.newWorkStealingPool(8);
    private final AtomicBoolean isActivePool = new AtomicBoolean(true);

    @PreDestroy
    public void clean() {
        executorService.shutdown();
    }

    public ExecutorService get() {
        return executorService;
    }

    public ForkJoinPool getPool() {
        return (ForkJoinPool) executorService;
    }

    public boolean isActive() {
        return getPool().getActiveThreadCount() > 0;
    }

    public boolean isNotStopped() {
        return isActivePool.get();
    }

    public void reload() {
        isActivePool.set(true);
    }

    public void shutdownAndStopTasks() {
        isActivePool.set(false);
    }

    public void pushTask(ScanTaskDto task) {
        tasksQueueService.push(task);
    }

    public ScanTaskDto popTask() {
        return tasksQueueService.pop();
    }

    public boolean isNotEmptyQueue() {
        return !tasksQueueService.isEmpty();
    }

    public void cleanQueue() {
        tasksQueueService.deleteAllTasks();
    }

}
