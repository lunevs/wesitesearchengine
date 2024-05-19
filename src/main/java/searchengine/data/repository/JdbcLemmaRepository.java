package searchengine.data.repository;

import searchengine.data.dto.common.LemmaDto;
import searchengine.data.dto.search.LemmaFrequencyDto;

import java.util.List;
import java.util.Set;

public interface JdbcLemmaRepository {

    void saveAll(List<LemmaDto> dtoList);
    List<LemmaDto> findAllByNamesAndSiteId(Set<String> names, int siteId);
    void deleteAllBySiteId(int siteId);
    List<LemmaFrequencyDto> getLemmasFrequency(Set<String> lemmas, List<Integer> sitesIds);
}
