package searchengine.services.scanner;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import searchengine.data.dto.ScanTaskDto;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class TasksQueueService {

    private final Set<ScanTaskDto> tasksList = Collections.newSetFromMap(new ConcurrentHashMap<ScanTaskDto, Boolean>());

    public boolean push(ScanTaskDto taskDto) {
        log.info(Thread.currentThread().getName() + " add url to tasks list: " + taskDto.getFullUrl());
        return tasksList.add(taskDto);
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
}
