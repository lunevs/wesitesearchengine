package searchengine.services.scanner;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import searchengine.data.dto.scanner.PageDto;
import searchengine.data.dto.scanner.PageParseResultDto;
import searchengine.data.dto.scanner.ScanTaskDto;
import searchengine.data.repository.JdbcPageRepository;
import searchengine.tools.PageParser;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class PageService {

    private final JdbcPageRepository pageRepository;

    public void deleteAll(int siteId) {
        pageRepository.deleteAllBySiteId(siteId);
    }

    public PageParseResultDto parseAndSavePage(ScanTaskDto taskDto) {
        log.info("{} parse page: {}", Thread.currentThread().getName(), taskDto.getFullUrl());
        PageParser pageParser = new PageParser(taskDto);
        try {
            PageParseResultDto resultDto = pageParser.connect();
            PageDto savedPage = save(resultDto.getPage());
            resultDto.setPage(savedPage);
            Thread.sleep(200);
            return resultDto;
        } catch (IOException | InterruptedException e) {
            // TODO create normal exception
            throw new RuntimeException(e.getMessage());
        }

    }

    public PageDto save(PageDto pageDto) {
        return pageRepository.save(pageDto);
    }

    public Set<Integer> getPagesWithAllLemmas(Set<Integer> lemmaIds) {
        if (lemmaIds.isEmpty()) {
            // TODO will return all pages
            return Collections.emptySet();
        }
        List<PageDto> resultPages = pageRepository.getPagesWithAllLemmas(lemmaIds);
        return resultPages.stream().map(PageDto::getId).collect(Collectors.toSet());
    }
}
