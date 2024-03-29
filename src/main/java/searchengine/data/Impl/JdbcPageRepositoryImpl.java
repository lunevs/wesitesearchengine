package searchengine.data.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.stereotype.Repository;
import searchengine.data.dto.PageDto;
import searchengine.data.repository.JdbcPageRepository;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class JdbcPageRepositoryImpl implements JdbcPageRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    private final static String insertSqlStatement = "insert into page (site_id, path, code, content) " +
            "VALUES (:siteId, :pagePath, :responseCode, :pageContent) " +
            "ON DUPLICATE KEY UPDATE content = :pageContent";

    private final static String deleteSqlStatement = "delete from page where site_id = :siteId";


    @Override
    public void saveAll(List<PageDto> pages) {
        splitByBatches(pages, 100);
    }

    @Override
    public void deleteAllBySiteId(int siteId) {
        SqlParameterSource params = new MapSqlParameterSource("siteId", siteId);
        jdbcTemplate.update(deleteSqlStatement, params);
    }

    private void batchUpdate(List<PageDto> pages) {
        SqlParameterSource[] params = SqlParameterSourceUtils.createBatch(pages);
        jdbcTemplate.batchUpdate(insertSqlStatement, params);
    }

    private void splitByBatches(List<PageDto> items, int batchSize) {
        int cnt = 0;
        List<PageDto> batch = new ArrayList<>(batchSize);
        for (PageDto item : items) {
            if (++cnt % batchSize == 0) {
                batchUpdate(batch);
                batch = new ArrayList<>(batchSize);
            }
            batch.add(item);
        }
        batchUpdate(batch);
    }
}
