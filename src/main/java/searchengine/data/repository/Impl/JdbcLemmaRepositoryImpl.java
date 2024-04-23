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
import searchengine.data.dto.FinalSearchResultDto;
import searchengine.data.dto.LemmaCounterDto;
import searchengine.data.dto.LemmaDto;
import searchengine.data.dto.LemmaFrequencyDto;
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
    private Resource searchPagesForLemma;
    private Resource getLemmasFrequency;
    private Resource getFinalSearchResults;

    @Override
    public void saveAll(List<LemmaDto> dtoList) {
        SqlParameterSource[] params = SqlParameterSourceUtils.createBatch(dtoList);
        jdbcTemplate.batchUpdate(ResourceUtils.getString(createLemma), params);
        log.info("{} saved batch of {} lemmas", Thread.currentThread().getName(), dtoList.size());
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

    @Override
    public List<LemmaCounterDto> searchPagesForLemma(String lemmaName) {
        SqlParameterSource params = new MapSqlParameterSource("lemmaName", lemmaName);
        return jdbcTemplate.query(
                ResourceUtils.getString(searchPagesForLemma),
                params,
                (rs, rowNum) -> new LemmaCounterDto()
                        .setLemmaId(rs.getInt("lemma_id"))
                        .setLemmaName(rs.getString("lemma"))
                        .setSiteId(rs.getInt("site_id"))
                        .setSiteUrl(rs.getString("url"))
                        .setPageId(rs.getInt("page_id"))
                        .setPagePath(rs.getString("path"))
                        .setCountPerPage(rs.getInt("lemma_rank"))
                        .setCountPerSite(rs.getInt("frequency")));
    }

    @Override
    public List<LemmaFrequencyDto> getLemmasFrequency(Set<String> lemmas) {
        Map<String, Object> map = new HashMap<>();
        map.put("lemmas", lemmas);
        map.put("lemmasCount", lemmas.size());
        return jdbcTemplate.query(
                ResourceUtils.getString(getLemmasFrequency),
                new MapSqlParameterSource(map),
                (rs, rowNum) -> new LemmaFrequencyDto()
                        .setSiteId(rs.getInt("site_id"))
                        .setLemmaId(rs.getInt("id"))
                        .setLemmaName(rs.getString("lemma"))
                        .setTotalSitePages(rs.getInt("total_pages"))
                        .setTotalPagesWithLemma(rs.getInt("total_pages_with_lemma"))
                        .setLemmaFrequency((float) rs.getInt("total_pages_with_lemma")/rs.getInt("total_pages")));
    }

    @Override
    public List<FinalSearchResultDto> getFinalSearchResults(Set<Integer> lemmasIds, Set<Integer> pagesIds) {
        Map<String, Object> map = new HashMap<>();
        map.put("lemmasIds", lemmasIds);
        map.put("pagesIds", pagesIds);
        return jdbcTemplate.query(
                ResourceUtils.getString(getFinalSearchResults),
                new MapSqlParameterSource(map),
                (rs, rowNum) -> new FinalSearchResultDto()
                        .setPageId(rs.getInt("page_id"))
                        .setAbsFrequency(rs.getInt("abs_frequency"))
                        .setPagePath(rs.getString("path"))
                        .setPageContent(rs.getString("content"))
                        .setSiteUrl(rs.getString("url"))
                        .setSiteName(rs.getString("name")));
    }
}
