package searchengine.services.scanner;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import searchengine.data.dto.PageDto;
import searchengine.data.dto.PageParseResultDto;
import searchengine.data.dto.ScanTaskDto;
import searchengine.data.repository.JdbcPageRepository;
import searchengine.tools.PageParser;

import java.io.IOException;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class PageService {

    private final JdbcPageRepository pageRepository;

    public void deleteAll(int siteId) {
        pageRepository.deleteAllBySiteId(siteId);
    }

    public PageParseResultDto parseAndSavePage(ScanTaskDto taskDto) {
        log.info(Thread.currentThread().getName() + " parse page: " + taskDto.getFullUrl());
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

    public void save(int siteId, String path, int responseCode, String pageContent) {
        pageRepository.save(new PageDto()
                .setSiteId(siteId)
                .setPagePath(path)
                .setResponseCode(responseCode)
                .setPageContent(pageContent));
    }

    public PageDto save(PageDto pageDto) {
        return pageRepository.save(pageDto);
    }

    public void saveAll(List<PageDto> pageDtoList) {
        pageRepository.saveAll(pageDtoList);
    }
}
