package searchengine.services.search;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import searchengine.data.dto.FinalSearchResultDto;
import searchengine.data.dto.LemmaCounterDto;
import searchengine.data.dto.LemmaFrequencyDto;
import searchengine.data.repository.JdbcLemmaRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class LemmaService {

    private final JdbcLemmaRepository lemmaRepository;

    public List<LemmaCounterDto> searchPagesForLemma(String lemmaName) {
        return lemmaRepository.searchPagesForLemma(lemmaName);
    }

    public List<LemmaCounterDto> searchPagesForAllLemmas(Set<String> lemmas) {
        List<LemmaCounterDto> result = new ArrayList<>();
        lemmas.forEach(lemma -> result.addAll(searchPagesForLemma(lemma)));
        return result;
    }

    public List<LemmaFrequencyDto> getLemmasFrequency(Set<String> lemmas) {
        return lemmaRepository.getLemmasFrequency(lemmas);
    }

    public List<FinalSearchResultDto> getFinalSearchResultDto(Set<Integer> lemmasIds, Set<Integer> pagesIds) {
        return lemmaRepository.getFinalSearchResults(lemmasIds, pagesIds);
    }
}
