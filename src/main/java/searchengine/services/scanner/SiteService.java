package searchengine.services.scanner;

import searchengine.data.model.Site;
import searchengine.data.model.SiteStatus;

import java.util.List;
import java.util.Optional;

public interface SiteService {

    List<Site> findAll();
    Site create(String siteUrl, String siteName);
    void updateSiteStatus(int siteId, SiteStatus status, String error);
    void deleteAllSiteDependencies(int siteId);
    Optional<Site> findSiteByUrl(String siteUrl);

}
