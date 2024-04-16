package searchengine.services.scanner;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import searchengine.data.dto.SiteDto;
import searchengine.data.model.Site;
import searchengine.data.model.SiteParameters;
import searchengine.data.model.SiteStatus;
import searchengine.data.repository.JdbcSiteRepository;
import searchengine.services.search.LemmaFinderService;
import searchengine.services.search.SearchIndexService;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class SiteService {

    private final JdbcSiteRepository jdbcSiteRepository;
    private final PageService pageService;
    private final LemmaFinderService lemmaService;
    private final SearchIndexService searchIndexService;

    public int prepareSiteToStartScanning(SiteParameters parameters) {
        int siteId = getSiteByUrlOrCreate(parameters.getUrl(), parameters.getName());
        updateSiteStatus(siteId, SiteStatus.INDEXING, null);
        searchIndexService.deleteAllBySite(siteId);
        lemmaService.deleteAllLemmasForSite(siteId);
        pageService.deleteAll(siteId);
        return siteId;
    }

    public void updateSiteStatus(int siteId, SiteStatus status, String error) {
        if (error != null && !error.isBlank()) {
            log.error(Thread.currentThread().getName() + " page parse error: " + error);
        }
        SiteDto siteDto = new SiteDto(siteId, status.name(), LocalDateTime.now(), error);
        jdbcSiteRepository.updateSiteStatus(siteDto);
    }

    public void markAllSitesAsFailed() {
        log.info(Thread.currentThread().getName() + " all sites are FAILED");
        jdbcSiteRepository.updateAllSitesStatusTo(SiteStatus.FAILED);
    }

    public void markAllSitesAsIndexed() {
        log.info(Thread.currentThread().getName() + " all sites are INDEXED");
        jdbcSiteRepository.updateAllSitesStatusTo(SiteStatus.INDEXED);
    }

    public int getSiteByUrlOrCreate(String siteUrl, String siteName) {
        Site savedSite = jdbcSiteRepository.findSiteByUrl(siteUrl);
        if (savedSite == null) {
            return createNewSite(siteUrl, siteName).getId();
        }
        return savedSite.getId();
    }

    private Site createNewSite(String siteUrl, String siteName) {
        int siteId = jdbcSiteRepository.save(siteUrl, siteName);
        return new Site(siteId, siteUrl, siteName);
    }
}
