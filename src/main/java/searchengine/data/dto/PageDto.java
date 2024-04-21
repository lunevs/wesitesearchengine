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

    public PageDto(int siteId, String pagePath, int responseCode, String pageContent) {
        this.siteId = siteId;
        this.pagePath = pagePath;
        this.responseCode = responseCode;
        this.pageContent = pageContent;
    }
}
