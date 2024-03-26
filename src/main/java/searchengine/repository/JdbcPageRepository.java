package searchengine.repository;

import searchengine.model.Page;

import java.util.List;
import java.util.Set;

public interface JdbcPageRepository {
    void saveAll(List<Page> pages);
}
