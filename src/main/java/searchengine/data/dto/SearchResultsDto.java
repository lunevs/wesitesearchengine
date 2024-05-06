package searchengine.data.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class SearchResultsDto {

    private int pageId;

    private int absFrequency;
    private Double relFrequency;

    private String pagePath;
    private String pageTitle;
    private String pageContent;

    private String siteUrl;
    private String siteName;

    private String snippet;
}
