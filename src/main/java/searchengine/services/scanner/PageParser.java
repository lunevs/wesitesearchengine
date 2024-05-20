package searchengine.services.scanner;

import searchengine.data.dto.scanner.PageParseResultDto;
import searchengine.data.dto.scanner.ScanTaskDto;

public interface PageParser {

    PageParseResultDto call(ScanTaskDto taskDto);
    PageParseResultDto scanPage(ScanTaskDto taskDto);

}
