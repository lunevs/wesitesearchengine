package searchengine.services.search;

import searchengine.data.dto.search.LemmaFrequencyDto;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class LemmasFilterImpl implements LemmasFilter {

    private static final float EXCLUDE_LIMIT = 0.9F;


    /**
     * Applies this function to the given argument.
     *
     * @param lemmaFrequencyDtos the function argument
     * @return the function result
     */
    @Override
    public Set<Integer> apply(List<LemmaFrequencyDto> lemmaFrequencyDtos) {
        return lemmaFrequencyDtos.stream()
                .filter(el -> el.getLemmaFrequency() < EXCLUDE_LIMIT)
                .sorted(Comparator.comparing(LemmaFrequencyDto::getLemmaFrequency))
                .map(LemmaFrequencyDto::getLemmaId)
                .collect(Collectors.toSet());
    }
}
