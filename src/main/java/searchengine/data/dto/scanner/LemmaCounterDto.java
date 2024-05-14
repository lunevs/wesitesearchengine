package searchengine.data.dto.scanner;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class LemmaCounterDto {
    private int lemmaId;
    private String lemmaName;
    private int siteId;
    private String siteUrl;
    private int pageId;
    private String pagePath;
    private int countPerPage;
    private int countPerSite;
}
