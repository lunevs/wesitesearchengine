package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import searchengine.data.dto.PageDto;
import searchengine.data.repository.JdbcPageRepository;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class SavePageTask implements Runnable {

    private final JdbcPageRepository pageRepository;
    private final Set<PageDto> batchSetOfPages;

    @Override
    public void run() {
        if (!batchSetOfPages.isEmpty()) {
            pageRepository.saveAll(batchSetOfPages.stream().toList());
            batchSetOfPages.clear();
        } else {
            System.out.println(Thread.currentThread().getName() + " received empty set to save");
        }
    }
}
