package searchengine.services.scanner;

import searchengine.data.dto.common.PageDto;

import java.util.Set;

public interface PageService {

    void deleteAllPagesBySiteId(int siteId);
    PageDto save(PageDto pageDto);
    Set<Integer> findPagesWithAllLemmas(Set<Integer> lemmaIds);

}
