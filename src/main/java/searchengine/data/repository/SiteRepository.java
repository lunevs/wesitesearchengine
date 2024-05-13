package searchengine.data.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import searchengine.data.model.Site;

import java.util.Optional;

@Repository
public interface SiteRepository extends CrudRepository<Site, Integer> {

    void updateSitesBySiteUrl(String siteUrl);
    Optional<Site> findSiteBySiteUrl(String siteUrl);

    @Modifying
    @Query("update Site s set s.status = :status")
    void updateAllSitesStatusTo(@Param(value = "status") String status);

}
