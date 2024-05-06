package searchengine.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class SearchResponseItem {
    private String site;
    private String siteName;
    private String uri;
    private String title;
    private String snippet;
    private double relevance;

    public static SearchResponseItem of(SearchResultsDto searchResultDto) {
        return new SearchResponseItem()
                .setSite(searchResultDto.getSiteUrl())
                .setSiteName(searchResultDto.getSiteName())
                .setUri(searchResultDto.getPagePath())
                .setTitle(searchResultDto.getPageTitle())
                .setSnippet(searchResultDto.getSnippet())
                .setRelevance(searchResultDto.getRelFrequency());
    }

}
