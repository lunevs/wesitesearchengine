package searchengine.data.repository.Impl;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import searchengine.data.dto.PageDto;
import searchengine.data.repository.JdbcPageRepository;
import searchengine.tools.ResourceUtils;

import java.util.HashMap;
import java.util.List;

@RequiredArgsConstructor
@Setter
@Slf4j
public class JdbcPageRepositoryImpl implements JdbcPageRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert pageSimpleJdbcInsert;

    private Resource createPage;
    private Resource deletePage;

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
    public void saveAll(List<PageDto> pages) {
        SqlParameterSource[] params = SqlParameterSourceUtils.createBatch(pages);
        jdbcTemplate.batchUpdate(ResourceUtils.getString(createPage), params);
        log.info(Thread.currentThread().getName() + " saved batch of " + pages.size() + " pages");
//        splitByBatches(pages, 100);
    }

    @Override
    public void deleteAllBySiteId(int siteId) {
        SqlParameterSource params = new MapSqlParameterSource("siteId", siteId);
        jdbcTemplate.update(ResourceUtils.getString(deletePage), params);
    }

//    private void batchUpdate(List<PageDto> pages) {
//        SqlParameterSource[] params = SqlParameterSourceUtils.createBatch(pages);
//        jdbcTemplate.batchUpdate(ResourceUtils.getString(createPage), params);
//    }
//
//    private void splitByBatches(List<PageDto> items, int batchSize) {
//        int cnt = 0;
//        List<PageDto> batch = new ArrayList<>(batchSize);
//        for (PageDto item : items) {
//            if (++cnt % batchSize == 0) {
//                batchUpdate(batch);
//                batch = new ArrayList<>(batchSize);
//            }
//            batch.add(item);
//        }
//        batchUpdate(batch);
//    }
}
