package searchengine.services.search;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import searchengine.data.dto.SearchIndexDto;
import searchengine.data.repository.JdbcSearchIndexRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchIndexService {

    private final JdbcSearchIndexRepository searchIndexRepository;

    public void saveAll(List<SearchIndexDto> searchIndexDtoList) {
        searchIndexRepository.saveAll(searchIndexDtoList);
        log.info("{} saved {} indexes", Thread.currentThread().getName(), searchIndexDtoList.size());
    }

    public void deleteAllBySite(int siteId) {
        log.info("{} delete all indexes for site: {}", Thread.currentThread().getName(), siteId);
        searchIndexRepository.deleteAllForSite(siteId);
    }
}
