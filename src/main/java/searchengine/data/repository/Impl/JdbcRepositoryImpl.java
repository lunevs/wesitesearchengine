package searchengine.data.repository.Impl;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import searchengine.data.dto.api.DetailedStatisticsItem;
import searchengine.data.dto.common.PageDto;
import searchengine.data.repository.JdbcRepository;
import searchengine.tools.ResourceUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
@Setter
public class JdbcRepositoryImpl implements JdbcRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    private Resource getDetailedSitesStatistics;
    private Resource getPagesContainsAllLemmas;

    @Override
    public List<DetailedStatisticsItem> getDetailedSitesStatistics() {
        return jdbcTemplate.query(
                ResourceUtils.getString(getDetailedSitesStatistics),
                new BeanPropertyRowMapper<>(DetailedStatisticsItem.class));
    }

    @Override
    public List<PageDto> getPagesContainsAllLemmas(Set<Integer> lemmaIds) {
        return jdbcTemplate.query(
                ResourceUtils.getString(getPagesContainsAllLemmas),
                Map.of("lemmas", lemmaIds, "lemmasCount", lemmaIds.size()),
                new BeanPropertyRowMapper<>(PageDto.class));
    }
}
