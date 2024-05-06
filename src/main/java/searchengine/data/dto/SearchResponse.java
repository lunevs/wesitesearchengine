package searchengine.data.dto;

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

    public static SearchResponse emptyResponse() {
        return new SearchResponse(false, 0, null);
    }

    public static SearchResponse of(List<SearchResponseItem> detailedSearchResults) {
        return new SearchResponse(!detailedSearchResults.isEmpty(), detailedSearchResults.size(), detailedSearchResults);
    }
}
