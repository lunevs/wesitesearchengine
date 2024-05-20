package searchengine.services.scanner;

import lombok.RequiredArgsConstructor;
import searchengine.data.model.Site;
import searchengine.data.model.SiteStatus;
import searchengine.data.repository.SiteRepository;
import searchengine.services.common.LemmaService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class SiteServiceImpl implements SiteService {

    private final PageService pageService;
    private final SearchIndexService searchIndexService;
    private final SiteRepository siteRepository;
    private final LemmaService lemmaService;

    @Override
    public void deleteAllSiteDependencies(int siteId) {
        searchIndexService.deleteAllBySiteId(siteId);
        lemmaService.deleteAllLemmasBySiteId(siteId);
        pageService.deleteAllPagesBySiteId(siteId);
        updateSiteStatus(siteId, SiteStatus.INDEXING, null);
    }

    @Override
    public Optional<Site> findSiteByUrl(String siteUrl) {
        return siteRepository.findSiteBySiteUrl(siteUrl);
    }

    @Override
    public void updateSiteStatus(int siteId, SiteStatus status, String error) {
        siteRepository
                .findById(siteId)
                .ifPresent(site -> {
                        site
                            .setStatus(status)
                            .setLastError(error)
                            .setStatusTime(LocalDateTime.now());
                        siteRepository.save(site);
                });
    }

    @Override
    public Site create(String siteUrl, String siteName) {
        Site newSite = new Site()
                .setSiteUrl(siteUrl)
                .setSiteName(siteName);
        return siteRepository.save(newSite);
    }

    @Override
    public List<Site> findAll() {
        return siteRepository.findAll();
    }
}
