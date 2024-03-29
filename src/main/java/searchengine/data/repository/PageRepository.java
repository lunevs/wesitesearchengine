package searchengine.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import searchengine.data.model.Page;

@Repository
public interface PageRepository extends JpaRepository<Page, Integer> {
    void deleteAllBySite_Id(int siteId);
}
