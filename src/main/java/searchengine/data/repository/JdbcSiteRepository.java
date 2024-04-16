package searchengine.data.repository;

import searchengine.data.dto.SiteDto;
import searchengine.data.model.Site;
import searchengine.data.model.SiteStatus;

import java.util.Optional;

public interface JdbcSiteRepository {
    void updateSiteStatus(SiteDto siteDto);
    int save(String url, String name);
    Optional<Site> findSiteByUrl(String url);
    void updateAllSitesStatusTo(SiteStatus status);
}
