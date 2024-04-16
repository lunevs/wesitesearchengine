package searchengine.data.repository.Impl;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import searchengine.data.dto.LemmaDto;
import searchengine.data.repository.JdbcLemmaRepository;
import searchengine.tools.ResourceUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
@Setter
@Slf4j
public class JdbcLemmaRepositoryImpl implements JdbcLemmaRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    private Resource createLemma;
    private Resource getAllLemmasByNames;
    private Resource getAllLemmasForSite;

    @Override
    public void saveAll(List<LemmaDto> dtoList) {
        SqlParameterSource[] params = SqlParameterSourceUtils.createBatch(dtoList);
        jdbcTemplate.batchUpdate(ResourceUtils.getString(createLemma), params);
        log.info(Thread.currentThread().getName() + " saved batch of " + dtoList.size() + " lemmas");
    }

    @Override
    public List<LemmaDto> getAllByNames(Set<String> names, int siteId) {
        Map<String, Object> map = new HashMap<>();
        map.put("names", names);
        map.put("siteId", siteId);
        return jdbcTemplate.query(
                ResourceUtils.getString(getAllLemmasByNames),
                new MapSqlParameterSource(map),
                new BeanPropertyRowMapper<>(LemmaDto.class));
    }

    @Override
    public List<LemmaDto> getAllLemmasForSite(int siteId) {
        SqlParameterSource params = new MapSqlParameterSource("siteId", siteId);
        return jdbcTemplate.query(
                ResourceUtils.getString(getAllLemmasForSite),
                params,
                (rs, rowNum) -> new LemmaDto()
                        .setId(rs.getInt("id"))
                        .setLemma(rs.getString("lemma"))
                        .setFrequency(rs.getInt("frequency"))
                        .setSiteId(rs.getInt("site_id")));
    }

    @Override
    public void deleteAllForSite(int siteId) {
        String sql = "delete from lemma where site_id = :siteId";
        SqlParameterSource params = new MapSqlParameterSource("siteId", siteId);
        jdbcTemplate.update(sql, params);
    }

}
