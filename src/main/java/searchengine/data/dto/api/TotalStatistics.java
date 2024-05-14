package searchengine.data.dto.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class TotalStatistics {
    private int sites;
    private int pages;
    private int lemmas;
    private boolean indexing;

}
