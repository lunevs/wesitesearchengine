package searchengine.data.repository.Impl;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import searchengine.data.dto.common.LemmaDto;
import searchengine.data.dto.search.LemmaFrequencyDto;
import searchengine.data.repository.JdbcLemmaRepository;
import searchengine.tools.ResourceUtils;

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
    private Resource getLemmasFrequency;

    @Override
    public void saveAll(List<LemmaDto> dtoList) {
        SqlParameterSource[] params = SqlParameterSourceUtils.createBatch(dtoList);
        jdbcTemplate.batchUpdate(ResourceUtils.getString(createLemma), params);
    }

    @Override
    public List<LemmaDto> findAllByNamesAndSiteId(Set<String> names, int siteId) {
        return jdbcTemplate.query(
                ResourceUtils.getString(getAllLemmasByNames),
                Map.of("names", names, "siteId", siteId),
                new BeanPropertyRowMapper<>(LemmaDto.class));
    }

    @Override
    public void deleteAllBySiteId(int siteId) {
        String sql = "delete from lemma where site_id = :siteId";
        jdbcTemplate.update(sql, Map.of("siteId", siteId));
    }

    @Override
    public List<LemmaFrequencyDto> getLemmasFrequency(Set<String> lemmas, List<Integer> sitesIds) {
        return jdbcTemplate.query(
                ResourceUtils.getString(getLemmasFrequency),
                Map.of("lemmas", lemmas, "lemmasCount", lemmas.size(), "sites", sitesIds),
                new BeanPropertyRowMapper<>(LemmaFrequencyDto.class));
    }

}
