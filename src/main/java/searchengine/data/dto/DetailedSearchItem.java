package searchengine.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class DetailedSearchItem {
    private String site;
    private String siteName;
    private String uri;
    private String title;
    private String snippet;
    private double relevance;

    public static DetailedSearchItem of(FinalSearchResultDto searchResultDto, String title, String snippet) {
        return new DetailedSearchItem()
                .setSite(searchResultDto.getSiteUrl())
                .setSiteName(searchResultDto.getSiteName())
                .setUri(searchResultDto.getPagePath())
                .setTitle(title)
                .setSnippet(snippet)
                .setRelevance(searchResultDto.getRelFrequency());
    }

}
