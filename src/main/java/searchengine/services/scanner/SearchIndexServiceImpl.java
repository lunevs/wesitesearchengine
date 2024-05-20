package searchengine.services.scanner;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import searchengine.data.dto.common.LemmaDto;
import searchengine.data.dto.search.SearchIndexDto;
import searchengine.data.repository.JdbcSearchIndexRepository;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
public class SearchIndexServiceImpl implements SearchIndexService {

    private final JdbcSearchIndexRepository searchIndexRepository;

    @Override
    public void saveAll(List<LemmaDto> savedLemmas, int pageId, Map<String, Integer> lemmas) {
        List<SearchIndexDto> searchIndexDtos = savedLemmas.stream()
                .map(el -> new SearchIndexDto()
                        .setPageId(pageId)
                        .setLemmaId(el.getId())
                        .setLemmaRank(Float.valueOf(lemmas.get(el.getLemma())))
                )
                .toList();
        searchIndexRepository.saveAll(searchIndexDtos);
        log.info("{} сохранен пакет из {} индексов для страницы {}", Thread.currentThread().getName(), searchIndexDtos.size(), pageId);
    }

    @Override
    public void deleteAllBySiteId(int siteId) {
        searchIndexRepository.deleteAllForSite(siteId);
        log.info("{} удалены все индексы для сайта {}", Thread.currentThread().getName(), siteId);
    }
}
