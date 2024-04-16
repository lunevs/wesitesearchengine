package searchengine.data.repository;

import searchengine.data.dto.SearchIndexDto;

import java.util.List;

public interface JdbcSearchIndexRepository {

    void saveAll(List<SearchIndexDto> indexDtoList);
    void deleteAllForSite(int siteId);
}
