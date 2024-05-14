package searchengine.data.dto.search;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@NoArgsConstructor
@Data
@Accessors(chain = true)
public class SearchIndexDto {

    private Integer id;
    private Integer pageId;
    private Integer lemmaId;
    private Float lemmaRank;
}
