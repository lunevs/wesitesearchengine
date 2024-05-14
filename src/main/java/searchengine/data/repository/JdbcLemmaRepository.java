package searchengine.data.repository;

import searchengine.data.dto.scanner.LemmaCounterDto;
import searchengine.data.dto.scanner.LemmaDto;
import searchengine.data.dto.search.LemmaFrequencyDto;

import java.util.List;
import java.util.Set;

public interface JdbcLemmaRepository {

    void saveAll(List<LemmaDto> dtoList);
    List<LemmaDto> getAllByNames(Set<String> names, int siteId);
    void deleteAllForSite(int siteId);
    List<LemmaFrequencyDto> getLemmasFrequency(Set<String> lemmas);
}
