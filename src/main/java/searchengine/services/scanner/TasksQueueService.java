package searchengine.services.scanner;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import searchengine.data.dto.scanner.ScanTaskDto;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class TasksQueueService {

    private final Set<ScanTaskDto> tasksList = Collections.newSetFromMap(new ConcurrentHashMap<ScanTaskDto, Boolean>());

    public void push(ScanTaskDto taskDto) {
        log.info("{} add url to tasks list: {}", Thread.currentThread().getName(), taskDto.getFullUrl());
        tasksList.add(taskDto);
    }

    public int size() {
        return tasksList.size();
    }

    public boolean isEmpty() {
        return tasksList.isEmpty();
    }

    public ScanTaskDto pop() {
        Optional<ScanTaskDto> pageScannerTask = tasksList.stream().findFirst();
        if (pageScannerTask.isPresent()) {
            ScanTaskDto task = pageScannerTask.get();
            tasksList.remove(task);
            return task;
        } else {
            return null;
        }
    }

    public void deleteAllTasks() {
        tasksList.clear();
    }
}
