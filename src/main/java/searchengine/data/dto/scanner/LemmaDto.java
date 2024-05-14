package searchengine.data.dto.scanner;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@NoArgsConstructor
@Setter
@Getter
@Accessors(chain = true)
public class LemmaDto {

    private int id;
    private int siteId;
    private String lemma;
    private int frequency;
}
