package searchengine.data.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import searchengine.data.model.Page;


@Getter
@Setter
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class PageDto {

    private int id;
    private int siteId;
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
