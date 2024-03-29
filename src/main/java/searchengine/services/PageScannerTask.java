package searchengine.services;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import searchengine.data.dto.PageDto;
import searchengine.data.dto.PageParseResultDto;
import searchengine.data.dto.ScanTaskDto;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;

@Getter
@RequiredArgsConstructor
public class PageScannerTask implements Callable<PageParseResultDto> {

    private final ScanTaskDto inputUrl;
    private Connection.Response response = null;

    @Override
    public PageParseResultDto call() {
        doConnect();

        PageParseResultDto resultDto = new PageParseResultDto();
        PageDto pageDto = new PageDto()
                .setSiteId(inputUrl.getSiteId())
                .setPagePath(inputUrl.getPath())
                .setResponseCode(getResponseStatus());
        try {
            Document doc = response.parse();
            System.out.println(Thread.currentThread().getName() + " check: " + inputUrl.getFullUrl());
            pageDto.setPageContent(doc.body().html());
            resultDto.setResultUrls(parseNextUrls(doc.select("a[href]")));
            Thread.sleep(200);
        } catch (IOException | InterruptedException e) {
            System.out.println(e.getMessage());
        }
        resultDto.setPage(pageDto);
        return resultDto;
    }

    private void doConnect() {
        try {
            response = Jsoup.connect(inputUrl.getFullUrl()).execute();
        } catch (IOException e) {
            System.out.println("io - "+e);
        }
    }

    private int getResponseStatus() {
        return response == null ? -1 : response.statusCode();
    }

    private Set<ScanTaskDto> parseNextUrls(Elements elements) {
        Set<ScanTaskDto> nextUrls = new HashSet<>();
        for (Element element : elements) {
            String currentPath = element.attr("href");
            if (currentPath.startsWith(inputUrl.getUrl())) {
                currentPath = currentPath.substring(inputUrl.getUrl().length());
            }
            if (currentPath.startsWith(inputUrl.getPath()) && !currentPath.equals(inputUrl.getPath())) {
                nextUrls.add(new ScanTaskDto(inputUrl.getUrl(), currentPath, inputUrl.getSiteId()));
            }
        }
        return nextUrls;
    }

}
