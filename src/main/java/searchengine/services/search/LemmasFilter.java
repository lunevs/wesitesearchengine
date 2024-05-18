package searchengine.services.search;

import searchengine.data.dto.search.LemmaFrequencyDto;

import java.util.List;
import java.util.Set;
import java.util.function.Function;

@FunctionalInterface
public interface LemmasFilter extends Function<List<LemmaFrequencyDto>, Set<Integer>> {
}
