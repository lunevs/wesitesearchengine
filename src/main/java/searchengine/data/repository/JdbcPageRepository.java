package searchengine.data.repository;

import searchengine.data.dto.PageDto;

import java.util.List;

public interface JdbcPageRepository {

    void deleteAllBySiteId(int siteId);
    PageDto save(PageDto pageDto);
}
