package searchengine.data.dto.search;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class LemmaFrequencyDto {

    private int siteId;
    private int lemmaId;
    private String lemmaName;
    private int totalSitePages;
    private int totalPagesWithLemma;
    private float lemmaFrequency;

}
