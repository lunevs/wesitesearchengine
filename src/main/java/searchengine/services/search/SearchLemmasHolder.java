package searchengine.services.search;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import searchengine.data.dto.SearchResultsDto;
import searchengine.data.dto.LemmaFrequencyDto;
import searchengine.data.repository.JdbcLemmaRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Getter
@Slf4j
@RequiredArgsConstructor
public class SearchLemmasHolder {

    private final JdbcLemmaRepository lemmaRepository;

    private static final float EXCLUDE_LIMIT = 0.9F;

    private List<LemmaFrequencyDto> searchLemmasList;

    public void init(Set<String> lemmas) {
        this.searchLemmasList = lemmaRepository.getLemmasFrequency(lemmas);
        log.info("constructed SearchQueryLemmasHolder: total lemmas {} and filtered lemmas: {} ", getAllLemmasIds().size(), getFilterLemmasIds().size());
    }

    public Set<Integer> getFilterLemmasIds() {
        return searchLemmasList.stream()
                .filter(el -> el.getLemmaFrequency() < EXCLUDE_LIMIT)
                .sorted(Comparator.comparing(LemmaFrequencyDto::getLemmaFrequency))
                .map(LemmaFrequencyDto::getLemmaId)
                .collect(Collectors.toSet());
    }

    public Set<Integer> getAllLemmasIds() {
        return searchLemmasList.stream()
                .map(LemmaFrequencyDto::getLemmaId)
                .collect(Collectors.toSet());
    }

    public Map<String, Set<Integer>> getResultsFrequencyBySites(List<SearchResultsDto> searchResults) {
        return searchResults.stream()
                .collect(Collectors.groupingBy(
                        SearchResultsDto::getSiteUrl,
                        Collectors.mapping(SearchResultsDto::getAbsFrequency, Collectors.toSet())));
    }

}
