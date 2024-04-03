package searchengine.data.Impl;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import searchengine.data.dto.SiteDto;
import searchengine.data.model.Site;
import searchengine.data.model.SiteRowMapper;
import searchengine.data.repository.JdbcSiteRepository;
import searchengine.tools.ResourceUtils;

import java.util.HashMap;
import java.util.Map;


@RequiredArgsConstructor
@Setter
public class JdbcSiteRepositoryImpl implements JdbcSiteRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    private Resource updateSiteStatus;
    private Resource createSite;
    private Resource findSiteByUrl;

    @Override
    public void updateSiteStatus(SiteDto siteDto) {
        SqlParameterSource params = new BeanPropertySqlParameterSource(siteDto);
        jdbcTemplate.update(ResourceUtils.getString(updateSiteStatus), params);
    }

    @Override
    public int save(String url, String name) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        Map<String, Object> params = new HashMap<>();
        params.put("siteUrl", url);
        params.put("siteName", name);
        jdbcTemplate.update(
                ResourceUtils.getString(createSite),
                new MapSqlParameterSource(params),
                keyHolder);
        return keyHolder.getKey().intValue();
    }

    @Override
    public Site findSiteByUrl(String url) {
        return jdbcTemplate.queryForObject(
                ResourceUtils.getString(findSiteByUrl),
                new MapSqlParameterSource("siteUrl", url),
                new SiteRowMapper());
    }
}
