package searchengine.data.Impl;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import searchengine.data.dto.SiteDto;
import searchengine.data.repository.JdbcSiteRepository;
import searchengine.tools.ResourceUtils;


@RequiredArgsConstructor
@Setter
public class JdbcSiteRepositoryImpl implements JdbcSiteRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    private Resource updateSiteStatus;

    @Override
    public void updateSiteStatus(SiteDto siteDto) {
        SqlParameterSource params = new BeanPropertySqlParameterSource(siteDto);
        jdbcTemplate.update(ResourceUtils.getString(updateSiteStatus), params);
    }
}
