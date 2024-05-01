package searchengine.services.search;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import searchengine.data.dto.FinalSearchResultDto;
import searchengine.data.dto.LemmaFrequencyDto;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Getter
public class SearchQueryLemmasHolder {

    private static final float EXCLUDE_LIMIT = 0.9F;
    private static final Logger log = LoggerFactory.getLogger(SearchQueryLemmasHolder.class);

    private List<LemmaFrequencyDto> searchLemmasList;

    public void init(List<LemmaFrequencyDto> searchLemmasFrequency) {
        this.searchLemmasList = searchLemmasFrequency;
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

    public Map<String, Set<Integer>> getResultsFrequencyBySites(List<FinalSearchResultDto> searchResults) {
        return searchResults.stream()
                .collect(Collectors.groupingBy(
                        FinalSearchResultDto::getSiteUrl,
                        Collectors.mapping(FinalSearchResultDto::getAbsFrequency, Collectors.toSet())));
    }

}
