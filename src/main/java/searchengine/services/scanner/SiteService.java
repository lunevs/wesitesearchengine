package searchengine.services.scanner;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import searchengine.data.dto.SiteDto;
import searchengine.data.model.Site;
import searchengine.data.model.SiteParameters;
import searchengine.data.model.SiteStatus;
import searchengine.data.repository.JdbcSiteRepository;
import searchengine.services.search.LemmaParserService;
import searchengine.services.search.SearchIndexService;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SiteService {

    private final JdbcSiteRepository jdbcSiteRepository;
    private final PageService pageService;
    private final LemmaParserService lemmaService;
    private final SearchIndexService searchIndexService;

    public int prepareSiteToStartScanning(SiteParameters parameters) {
        int siteId = getSiteByUrlOrCreate(parameters.getUrl(), parameters.getName());
        searchIndexService.deleteAllBySite(siteId);
        lemmaService.deleteAllLemmasForSite(siteId);
        pageService.deleteAll(siteId);
        updateSiteStatus(siteId, SiteStatus.INDEXING, null);
        return siteId;
    }

    public void updateSiteStatus(int siteId, SiteStatus status, String error) {
        if (error != null && !error.isBlank()) {
            log.error("{} page parse error: {}", Thread.currentThread().getName(), error);
        }
        SiteDto siteDto = new SiteDto(siteId, status.name(), LocalDateTime.now(), error);
        jdbcSiteRepository.updateSiteStatus(siteDto);
    }

    public void markAllSitesAsFailed() {
        log.info("{} all sites are FAILED", Thread.currentThread().getName());
        jdbcSiteRepository.updateAllSitesStatusTo(SiteStatus.FAILED);
    }

    public void markAllSitesAsIndexed() {
        log.info("{} all sites are INDEXED", Thread.currentThread().getName());
        jdbcSiteRepository.updateAllSitesStatusTo(SiteStatus.INDEXED);
    }

    public int getSiteByUrlOrCreate(String siteUrl, String siteName) {
        Optional<Site> savedSite = jdbcSiteRepository.findSiteByUrl(siteUrl);
        return savedSite.map(Site::getId).orElseGet(() -> createNewSite(siteUrl, siteName).getId());
    }

    private Site createNewSite(String siteUrl, String siteName) {
        int siteId = jdbcSiteRepository.save(siteUrl, siteName);
        return new Site(siteId, siteUrl, siteName);
    }
}
