package searchengine.data.repository.Impl;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import searchengine.data.dto.api.DetailedStatisticsItem;
import searchengine.data.repository.JdbcRepository;
import searchengine.tools.ResourceUtils;

import java.util.List;

@RequiredArgsConstructor
@Setter
public class JdbcRepositoryImpl implements JdbcRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    private Resource getDetailedSitesStatistics;


    @Override
    public List<DetailedStatisticsItem> getDetailedSitesStatistics() {
        return jdbcTemplate.query(
                ResourceUtils.getString(getDetailedSitesStatistics),
                new BeanPropertyRowMapper<>(DetailedStatisticsItem.class));
    }
}
