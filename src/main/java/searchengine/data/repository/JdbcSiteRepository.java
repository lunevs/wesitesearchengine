package searchengine.data.repository;

import searchengine.data.dto.SiteDto;
import searchengine.data.model.Site;

public interface JdbcSiteRepository {
    void updateSiteStatus(SiteDto siteDto);
    int save(String url, String name);
    Site findSiteByUrl(String url);
}
