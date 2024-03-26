package searchengine.repository;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.stereotype.Repository;
import searchengine.dto.statistics.PageDto;
import searchengine.model.Page;

import java.util.ArrayList;
import java.util.List;

@Repository
public class JdbcPageRepositoryImpl implements JdbcPageRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    private final static String insertSqlStatement = "insert into page (site_id, path, code, content) " +
            "VALUES (:siteId, :pagePath, :responseCode, :pageContent) " +
            "ON DUPLICATE KEY UPDATE content = :pageContent";

    public JdbcPageRepositoryImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void saveAll(List<Page> pages) {
        splitByBatches(pages, 100);
    }

    private void batchUpdate(List<Page> pages) {
        List<PageDto> pageDtos = pages.stream().map(PageDto::of).toList();
        SqlParameterSource[] params = SqlParameterSourceUtils.createBatch(pageDtos);
        jdbcTemplate.batchUpdate(insertSqlStatement, params);
    }

    private void splitByBatches(List<Page> items, int batchSize) {
        int cnt = 0;
        List<Page> batch = new ArrayList<>(batchSize);
        for (Page item : items) {
            if (++cnt % batchSize == 0) {
                batchUpdate(batch);
                batch = new ArrayList<>(batchSize);
            }
            batch.add(item);
        }
        batchUpdate(batch);
    }
}
