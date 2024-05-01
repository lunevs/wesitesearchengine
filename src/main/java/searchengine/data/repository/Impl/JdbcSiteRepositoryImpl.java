package searchengine.data.repository.Impl;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import searchengine.data.dto.SiteDto;
import searchengine.data.model.Site;
import searchengine.data.model.SiteRowMapper;
import searchengine.data.model.SiteStatus;
import searchengine.data.repository.JdbcSiteRepository;
import searchengine.tools.ResourceUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@RequiredArgsConstructor
@Setter
public class JdbcSiteRepositoryImpl implements JdbcSiteRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert siteSimpleJdbcInsert;

    private Resource updateSiteStatus;
    private Resource findSiteByUrl;
    private Resource updateAllSitesStatusTo;

    @Override
    public void updateAllSitesStatusTo(SiteStatus status) {
        jdbcTemplate.update(
                ResourceUtils.getString(updateAllSitesStatusTo),
                Map.of("statusName", status.name()));
    }

    @Override
    public void updateSiteStatus(SiteDto siteDto) {
        jdbcTemplate.update(
                ResourceUtils.getString(updateSiteStatus),
                new BeanPropertySqlParameterSource(siteDto));
    }

    @Override
    public int save(String url, String name) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("url", url);
        params.put("name", name);
        params.put("status", SiteStatus.INDEXING.name());
        params.put("last_error", "");
        return siteSimpleJdbcInsert.executeAndReturnKey(params).intValue();
    }

    @Override
    public Optional<Site> findSiteByUrl(String url) {
        List<Site> results = jdbcTemplate.query(
                ResourceUtils.getString(findSiteByUrl),
                new MapSqlParameterSource("siteUrl", url),
                new SiteRowMapper());
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

}
