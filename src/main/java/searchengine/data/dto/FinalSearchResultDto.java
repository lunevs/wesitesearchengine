package searchengine.data.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class FinalSearchResultDto {

    private int pageId;
    private int absFrequency;
    private String pagePath;
    private String pageContent;
    private String siteUrl;
    private String siteName;
    private Double relFrequency;
}
