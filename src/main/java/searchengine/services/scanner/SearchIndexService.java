package searchengine.services.scanner;

import searchengine.data.dto.common.LemmaDto;

import java.util.List;
import java.util.Map;

public interface SearchIndexService {

    void saveAll(List<LemmaDto> savedLemmas, int pageId, Map<String, Integer> lemmas);
    void deleteAllBySite(int siteId);

}
