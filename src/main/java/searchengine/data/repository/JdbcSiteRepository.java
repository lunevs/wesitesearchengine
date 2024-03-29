package searchengine.data.repository;

import searchengine.data.dto.SiteDto;

public interface JdbcSiteRepository {
    void updateSiteStatus(SiteDto siteDto);
}
