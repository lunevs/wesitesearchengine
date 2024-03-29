package searchengine.data.dto;

import lombok.Data;

import java.util.Set;

@Data
public class PageParseResultDto {

    private PageDto page;
    private Set<ScanTaskDto> resultUrls;

}
