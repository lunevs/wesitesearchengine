package searchengine.services.common;

import searchengine.data.dto.common.LemmaDto;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface LemmaService {

    void deleteAllLemmasBySiteId(int siteId);
    List<LemmaDto> getAllByNames(Set<String> names, int siteId);
    void saveLemmas(Map<String, Integer> lemmas, int siteId);

}
