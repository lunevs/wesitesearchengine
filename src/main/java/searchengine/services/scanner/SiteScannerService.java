package searchengine.services.scanner;

import searchengine.config.SitesList;
import searchengine.data.dto.scanner.ScanTaskDto;

public interface SiteScannerService {

    void unexpectedStop();
    void startAllSitesScan(SitesList sitesList);
    void startOnePageScan(ScanTaskDto task);

}
