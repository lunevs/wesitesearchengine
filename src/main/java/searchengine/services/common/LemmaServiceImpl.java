package searchengine.services.common;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import searchengine.data.dto.common.LemmaDto;
import searchengine.data.repository.JdbcLemmaRepository;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
@Slf4j
public class LemmaServiceImpl implements LemmaService {

    private final JdbcLemmaRepository lemmaRepository;


    @Override
    public void deleteAllLemmasForSite(int siteId) {
        lemmaRepository.deleteAllBySiteId(siteId);
        log.info("{} удалениевсех лемм для сайта: {}", Thread.currentThread().getName(), siteId);
    }

    @Override
    public List<LemmaDto> getAllByNames(Set<String> names, int siteId) {
        return lemmaRepository.findAllByNamesAndSiteId(names, siteId);
    }

    @Override
    public void saveLemmas(Map<String, Integer> lemmas, int siteId) {
        List<LemmaDto> lemmaDtoList = lemmas.entrySet().stream()
                .map(el -> new LemmaDto()
                        .setLemma(el.getKey())
                        .setFrequency(el.getValue())
                        .setSiteId(siteId))
                .toList();
        lemmaRepository.saveAll(lemmaDtoList);
        log.info("{} сохранен Batch лемм ({} штук) для сайта {}", Thread.currentThread().getName(), lemmaDtoList.size(), siteId);
    }
}
