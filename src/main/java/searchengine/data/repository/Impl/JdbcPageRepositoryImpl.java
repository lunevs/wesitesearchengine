package searchengine.data.repository.Impl;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import searchengine.data.dto.scanner.PageDto;
import searchengine.data.repository.JdbcPageRepository;
import searchengine.tools.ResourceUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
@Setter
@Slf4j
public class JdbcPageRepositoryImpl implements JdbcPageRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert pageSimpleJdbcInsert;

    private Resource deletePage;
    private Resource getPagesWithAllLemmas;

    @Override
    public PageDto save(PageDto pageDto) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("site_id", pageDto.getSiteId());
        params.put("path", pageDto.getPagePath());
        params.put("code", pageDto.getResponseCode());
        params.put("content", pageDto.getPageContent());
        Number id = pageSimpleJdbcInsert.executeAndReturnKey(params);
        pageDto.setId(id.intValue());
        return pageDto;
    }

    @Override
    public List<PageDto> getPagesWithAllLemmas(Set<Integer> lemmaIds) {
        return jdbcTemplate.query(
                ResourceUtils.getString(getPagesWithAllLemmas),
                Map.of("lemmas", lemmaIds, "lemmasCount", lemmaIds.size()),
                new BeanPropertyRowMapper<>(PageDto.class));
    }

    @Override
    public void deleteAllBySiteId(int siteId) {
        jdbcTemplate.update(
                ResourceUtils.getString(deletePage),
                Map.of("siteId", siteId));
    }
}
