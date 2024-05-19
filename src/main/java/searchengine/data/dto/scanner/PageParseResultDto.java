package searchengine.data.dto.scanner;

import lombok.Data;
import lombok.experimental.Accessors;
import searchengine.data.dto.common.PageDto;

import java.util.Set;

@Data
@Accessors(chain = true)
public class PageParseResultDto {

    private PageDto page;
    private Set<ScanTaskDto> resultUrls;

}
