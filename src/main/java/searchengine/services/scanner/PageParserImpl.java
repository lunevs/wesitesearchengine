package searchengine.services.scanner;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.springframework.http.HttpStatus;
import searchengine.data.dto.common.PageDto;
import searchengine.data.dto.scanner.PageParseResultDto;
import searchengine.data.dto.scanner.ScanTaskDto;
import searchengine.services.common.ExecutorServiceHandler;
import searchengine.services.common.LemmaParser;
import searchengine.tools.ParserUtils;

import java.io.IOException;
import java.util.Collections;

@RequiredArgsConstructor
@Slf4j
public class PageParserImpl implements PageParser {

    private final ExecutorServiceHandler executorServiceHandler;
    private final PageService pageService;
    private final LemmaParser lemmaParser;

    @Override
    public PageParseResultDto call(ScanTaskDto taskDto) {
        if (executorServiceHandler.isNotStopped()) {
            PageParseResultDto resultDto = scanPage(taskDto);
            String pageText = Jsoup.parseBodyFragment(resultDto.getPage().getPageContent()).text();
            lemmaParser.parseLemmasFromPage(pageText, taskDto.getSiteId(), resultDto.getPage().getId());
            resultDto.getResultUrls().forEach(executorServiceHandler::pushTask);
            return resultDto;
        } else {
            return saveFailedTask(taskDto.getSiteId(), taskDto.getFullUrl(), taskDto.getPath(), HttpStatus.BAD_REQUEST.value(), "отменено пользователем");
        }
    }

    @Override
    public PageParseResultDto scanPage(ScanTaskDto taskDto) {
        ParserUtils parserUtils = new ParserUtils(taskDto);
        try {
            PageParseResultDto resultDto = parserUtils.connect();
            PageDto savedPage = pageService.save(resultDto.getPage());
            resultDto.setPage(savedPage);
            log.info("{} Страница успешно просканирована и сохранена: {}", Thread.currentThread().getName(), taskDto.getFullUrl());
            Thread.sleep(200);
            return resultDto;
        } catch (IOException | InterruptedException e) {
            return saveFailedTask(taskDto.getSiteId(), taskDto.getFullUrl(), taskDto.getPath(), parserUtils.getResponse().statusCode(), e.getMessage());
        }
    }

    private PageParseResultDto saveFailedTask(int siteId, String pageFullUrl, String pagePath, int errorCode, String errorMessage) {
        log.warn("{} Не удалось просканировать страницу {}, ошибка: {}", Thread.currentThread().getName(), pageFullUrl, errorMessage);
        PageDto errorDto = new PageDto(siteId, pagePath, errorCode, errorMessage);
        return new PageParseResultDto()
                .setPage(pageService.save(errorDto))
                .setResultUrls(Collections.emptySet());
    }

}
