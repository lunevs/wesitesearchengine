package searchengine.services.scanner;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Service;
import searchengine.data.dto.common.PageDto;
import searchengine.data.dto.scanner.PageParseResultDto;
import searchengine.data.dto.scanner.ScanTaskDto;
import searchengine.data.repository.JdbcRepository;
import searchengine.data.repository.PageRepository;
import searchengine.tools.PageParser;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PageService {

    private final PageRepository pageRepository;
    private final JdbcRepository jdbcRepository;
    private final SimpleJdbcInsert pageSimpleJdbcInsert;

    public void deleteAll(int siteId) {
        pageRepository.deleteAllBySiteId(siteId);
    }

    public PageParseResultDto scanPage(ScanTaskDto taskDto) {
        PageParser pageParser = new PageParser(taskDto);
        try {
            PageParseResultDto resultDto = pageParser.connect();
            PageDto savedPage = save(resultDto.getPage());
            resultDto.setPage(savedPage);
            log.warn("{} Страница успешно просканирована и сохранена: {}", Thread.currentThread().getName(), taskDto.getFullUrl());
            Thread.sleep(200);
            return resultDto;
        } catch (IOException | InterruptedException e) {
            log.warn("{} Не удалось просканировать страницу {}, ошибка: {}", Thread.currentThread().getName(), taskDto.getFullUrl(), e.getMessage());
            PageDto errorDto = new PageDto(taskDto.getSiteId(), taskDto.getPath(), pageParser.getResponse().statusCode(), e.getMessage());
            return new PageParseResultDto()
                    .setPage(save(errorDto))
                    .setResultUrls(Collections.emptySet());
        }

    }

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

    public Set<Integer> findPagesWithAllLemmas(Set<Integer> lemmaIds) {
        List<PageDto> resultPages = jdbcRepository.getPagesContainsAllLemmas(lemmaIds);
        return resultPages.stream().map(PageDto::getId).collect(Collectors.toSet());
    }
}
