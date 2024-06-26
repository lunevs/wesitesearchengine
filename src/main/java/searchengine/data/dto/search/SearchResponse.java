package searchengine.data.dto.search;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchResponse {
    private boolean result;
    private int count;
    private List<SearchResponseItem> data;

    public static SearchResponse of(List<SearchResponseItem> detailedSearchResults, int resultSize) {
        return new SearchResponse(!detailedSearchResults.isEmpty(), resultSize, detailedSearchResults);
    }
}
