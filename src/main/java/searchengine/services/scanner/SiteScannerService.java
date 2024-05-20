package searchengine.services.scanner;

import searchengine.config.SitesList;
import searchengine.data.dto.scanner.ScanTaskDto;

public interface SiteScannerService {

    void unexpectedStop();
    void scanAllPages(SitesList sitesList);
    void scanOnePage(ScanTaskDto task);

}
