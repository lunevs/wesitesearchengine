package searchengine.data.repository.Impl;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import searchengine.data.dto.SearchIndexDto;
import searchengine.data.repository.JdbcSearchIndexRepository;
import searchengine.tools.ResourceUtils;

import java.util.List;

@RequiredArgsConstructor
@Setter
@Slf4j
public class JdbcSearchIndexRepositoryImpl implements JdbcSearchIndexRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    private Resource saveListSearchIndex;

    @Override
    public void saveAll(List<SearchIndexDto> indexDtoList) {
        SqlParameterSource[] params = SqlParameterSourceUtils.createBatch(indexDtoList);
        jdbcTemplate.batchUpdate(ResourceUtils.getString(saveListSearchIndex), params);
        log.info(Thread.currentThread().getName() + " saved batch of " + indexDtoList.size() + " SearchIndexes");
    }

    @Override
    public void deleteAllForSite(int siteId) {
        String sql = "delete s from search_index s join page p on s.page_id = p.id where p.site_id = :siteId";
        SqlParameterSource params = new MapSqlParameterSource("siteId", siteId);
        jdbcTemplate.update(sql, params);
    }
}
