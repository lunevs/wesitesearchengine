package searchengine.dto.statistics;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import searchengine.model.Page;


@Getter
@Setter
@Accessors(chain = true)
public class PageDto {

    private int id;
    private Integer siteId;
    private String pagePath;
    private int responseCode;
    private String pageContent;

    public static PageDto of(Page page) {
        return new PageDto()
                .setId(page.getId())
                .setSiteId(page.getSite().getId())
                .setPagePath(page.getPagePath())
                .setResponseCode(page.getResponseCode())
                .setPageContent(page.getPageContent());
    }
}
