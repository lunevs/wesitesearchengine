package searchengine.data.repository.Impl;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import searchengine.data.dto.search.SearchIndexDto;
import searchengine.data.repository.JdbcSearchIndexRepository;
import searchengine.tools.ResourceUtils;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Setter
public class JdbcSearchIndexRepositoryImpl implements JdbcSearchIndexRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    private Resource saveListSearchIndex;
    private Resource deleteSearchIndexBySite;

    @Override
    public void saveAll(List<SearchIndexDto> indexDtoList) {
        SqlParameterSource[] params = SqlParameterSourceUtils.createBatch(indexDtoList);
        jdbcTemplate.batchUpdate(ResourceUtils.getString(saveListSearchIndex), params);
    }

    @Override
    public void deleteAllForSite(int siteId) {
        String sql = ResourceUtils.getString(deleteSearchIndexBySite);
        jdbcTemplate.update(sql, Map.of("siteId", siteId));
    }
}
