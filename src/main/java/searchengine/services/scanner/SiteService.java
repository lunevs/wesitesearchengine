package searchengine.services.scanner;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import searchengine.data.model.Site;
import searchengine.data.model.SiteParameters;
import searchengine.data.model.SiteStatus;
import searchengine.data.repository.SiteRepository;
import searchengine.services.common.LemmaService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SiteService {

    private final PageService pageService;
    private final SearchIndexService searchIndexService;
    private final SiteRepository siteRepository;
    private final LemmaService lemmaService;

    public int prepareSiteToStartScanning(SiteParameters parameters) {
        int siteId = getSiteByUrlOrCreate(parameters.getUrl(), parameters.getName());
        searchIndexService.deleteAllBySite(siteId);
        lemmaService.deleteAllLemmasForSite(siteId);
        pageService.deleteAllPagesBySiteId(siteId);
        updateSiteStatus(siteId, SiteStatus.INDEXING, null);
        return siteId;
    }

    public void endSiteScanning(int siteId, boolean isNotStopped) {
        updateSiteStatus(
                siteId,
                isNotStopped ? SiteStatus.INDEXED : SiteStatus.FAILED,
                isNotStopped ? "from executeScanTask" : "site scanning interrupted by user");
    }

    public Optional<Site> findSiteByUrl(String siteUrl) {
        return siteRepository.findSiteBySiteUrl(siteUrl);
    }

    public void updateSiteStatus(int siteId, SiteStatus status, String error) {
        log.info("{} call to updateSiteStatus: {} - {}", Thread.currentThread().getName(), siteId, status);
        siteRepository
                .findById(siteId)
                .ifPresent(site -> {
                        log.info("{} Site found. Update status: {}", Thread.currentThread().getName(), status);
                        site
                            .setStatus(status)
                            .setLastError(error)
                            .setStatusTime(LocalDateTime.now());
                        siteRepository.save(site);
                });
    }

    public int getSiteByUrlOrCreate(String siteUrl, String siteName) {
        Optional<Site> savedSite = siteRepository.findSiteBySiteUrl(siteUrl);
        return savedSite.map(Site::getId).orElseGet(() -> createNewSite(siteUrl, siteName).getId());
    }

    private Site createNewSite(String siteUrl, String siteName) {
        Site newSite = new Site()
                .setSiteUrl(siteUrl)
                .setSiteName(siteName);
        return siteRepository.save(newSite);
    }

    public List<Site> findAll() {
        return siteRepository.findAll();
    }
}
