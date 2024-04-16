package searchengine.data.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Set;

@Data
@Accessors(chain = true)
public class PageParseResultDto {

    private PageDto page;
    private Set<ScanTaskDto> resultUrls;

}
