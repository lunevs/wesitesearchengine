package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import searchengine.config.SiteParameter;
import searchengine.data.dto.ScanTaskDto;
import searchengine.data.dto.SiteDto;
import searchengine.data.model.Site;
import searchengine.data.model.SiteStatus;
import searchengine.data.repository.JdbcSiteRepository;
import searchengine.data.repository.SiteRepository;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;

@Service
@RequiredArgsConstructor
public class SiteScannerService {

    private final SiteRepository siteRepository;
    private final PageScannerService pageScannerService;
    private final SiteService siteService;


    public void start(SiteParameter parameter) {
        int siteId = siteRepository.findSiteBySiteUrl(parameter.getUrl()).orElseGet(() -> getNewSite(parameter)).getId();
        siteService.updateSiteStatus(siteId, SiteStatus.INDEXING, null);
        try {
            ScanTaskDto scanTaskDto = new ScanTaskDto(parameter.getUrl(), "/", siteId);
            pageScannerService.start(scanTaskDto);
            siteService.updateSiteStatus(siteId, SiteStatus.INDEXED, null);
        } catch (Exception e) {
            siteService.updateSiteStatus(siteId, SiteStatus.FAILED, e.getMessage());
        }
    }

    public void stop() {
        pageScannerService.stop();
    }

    private Site getNewSite(SiteParameter parameter) {
        Site newSite = new Site()
                .setSiteUrl(parameter.getUrl())
                .setSiteName(parameter.getName())
                .setStatus(SiteStatus.INDEXING)
                .setStatusTime(LocalDateTime.now());
        return siteRepository.save(newSite);
    }
}
