package searchengine.data.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import searchengine.data.model.Page;


@Getter
@Setter
@Accessors(chain = true)
public class PageDto {

    private Integer siteId;
    private String pagePath;
    private int responseCode;
    private String pageContent;

    public static PageDto of(Page page) {
        return new PageDto()
                .setSiteId(page.getSite().getId())
                .setPagePath(page.getPagePath())
                .setResponseCode(page.getResponseCode())
                .setPageContent(page.getPageContent());
    }
}
