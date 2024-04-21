package searchengine.tools;

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

@Getter
@RequiredArgsConstructor
public class PageParser {

    private final ScanTaskDto taskDto;

    private Connection.Response response = null;

    public PageParseResultDto connect() throws IOException {
        response = Jsoup.connect(taskDto.getFullUrl()).execute();
        Document doc = response.parse();
        String pageBody = doc.html();
        return new PageParseResultDto()
                .setResultUrls(parseNextUrls(doc.select("a[href]"), taskDto))
                .setPage(new PageDto(taskDto.getSiteId(), taskDto.getPath(), response.statusCode(), pageBody));
    }

    private Set<ScanTaskDto> parseNextUrls(Elements elements, ScanTaskDto taskDto) {
        Set<ScanTaskDto> nextUrls = new HashSet<>();
        for (Element element : elements) {
            String currentPath = element.attr("href");
            if (currentPath.startsWith(taskDto.getUrl())) {
                currentPath = currentPath.substring(taskDto.getUrl().length());
            }
            if (currentPath.startsWith(taskDto.getPath()) && !currentPath.equals(taskDto.getPath())) {
                nextUrls.add(new ScanTaskDto(taskDto.getUrl(), currentPath, taskDto.getSiteId()));
            }
        }
        return nextUrls;
    }
}
