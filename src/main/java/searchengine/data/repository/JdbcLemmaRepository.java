package searchengine.data.repository;

import searchengine.data.dto.LemmaDto;

import java.util.List;
import java.util.Set;

public interface JdbcLemmaRepository {

    void saveAll(List<LemmaDto> dtoList);
    List<LemmaDto> getAllByNames(Set<String> names, int siteId);
    List<LemmaDto> getAllLemmasForSite(int siteId);
    void deleteAllForSite(int siteId);
}
