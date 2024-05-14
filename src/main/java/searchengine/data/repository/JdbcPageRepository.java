package searchengine.data.repository;

import searchengine.data.dto.scanner.PageDto;

import java.util.List;
import java.util.Set;

public interface JdbcPageRepository {

    void deleteAllBySiteId(int siteId);
    PageDto save(PageDto pageDto);
    List<PageDto> getPagesWithAllLemmas(Set<Integer> lemmaIds);
}
