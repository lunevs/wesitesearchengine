package searchengine.services.scanner;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import searchengine.data.dto.common.PageDto;
import searchengine.data.repository.JdbcRepository;
import searchengine.data.repository.PageRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
public class PageServiceImpl implements PageService {

    private final PageRepository pageRepository;
    private final JdbcRepository jdbcRepository;
    private final SimpleJdbcInsert pageSimpleJdbcInsert;


    @Override
    public void deleteAllPagesBySiteId(int siteId) {
        pageRepository.deleteAllBySiteId(siteId);
        log.info("{} удалены все страницы для сайта: {}", Thread.currentThread().getName(), siteId);
    }

    @Override
    public PageDto save(PageDto pageDto) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("site_id", pageDto.getSiteId());
        params.put("path", pageDto.getPagePath());
        params.put("code", pageDto.getResponseCode());
        params.put("content", pageDto.getPageContent());
        Number id = pageSimpleJdbcInsert.executeAndReturnKey(params);
        pageDto.setId(id.intValue());
        log.info("{} сохранена новая страница {} для сайта: {}", Thread.currentThread().getName(), pageDto.getPagePath(), pageDto.getSiteId());
        return pageDto;
    }

    @Override
    public Set<Integer> findPagesWithAllLemmas(Set<Integer> lemmaIds) {
        List<PageDto> resultPages = jdbcRepository.getPagesContainsAllLemmas(lemmaIds);
        return resultPages.stream().map(PageDto::getId).collect(Collectors.toSet());
    }
}

