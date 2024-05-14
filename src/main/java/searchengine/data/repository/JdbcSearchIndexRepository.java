package searchengine.data.repository;

import searchengine.data.dto.search.SearchIndexDto;

import java.util.List;

public interface JdbcSearchIndexRepository {

    void saveAll(List<SearchIndexDto> indexDtoList);
    void deleteAllForSite(int siteId);
}
