package searchengine.data.repository;

import searchengine.data.dto.PageDto;

import java.util.List;

public interface JdbcPageRepository {

    void saveAll(List<PageDto> dtoList);
    void deleteAllBySiteId(int siteId);
    PageDto save(PageDto pageDto);
}
