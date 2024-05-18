package searchengine.services.search;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import searchengine.services.common.LemmaParserService;
import searchengine.tools.StringUtils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;

@Service
@RequiredArgsConstructor
public class SnippetService {

    private final LemmaParserService lemmaParserService;
    private final SearchQueryHolder searchQueryHolder;

    private String initialText;
    private final Map<String, Set<String>> textNormalFormsMap = new HashMap<>();


    public void init(String initialText) {
        this.initialText = initialText;
    }

    public String buildSnippet() {
        fillTextWordsMap();
        List<Integer> indexCombinations = findClosestWordCombination(findWordsIndexes());
        String snippet = StringUtils.createSnippet(initialText, indexCombinations);
        return boldSearchWords(indexCombinations, snippet);
    }

    private void fillTextWordsMap() {
        textNormalFormsMap.clear();
        for (String curWord : initialText.split("[\\s+\\-]")) {
            String normalForm = lemmaParserService.getNormalWordForm(curWord);
            if (!normalForm.isEmpty() && searchQueryHolder.getQueryAsLemmaList().contains(normalForm)) {
                if (textNormalFormsMap.containsKey(normalForm)) {
                    textNormalFormsMap.get(normalForm).add(curWord);
                } else {
                    textNormalFormsMap.put(normalForm, new HashSet<>(List.of(curWord)));
                }
            }
        }
    }

    private List<NavigableSet<Integer>> findWordsIndexes() {
        List<NavigableSet<Integer>> indexes = new ArrayList<>(textNormalFormsMap.size());
        for (Map.Entry<String, Set<String>> searchElement : textNormalFormsMap.entrySet()) {
            NavigableSet<Integer> curIndexes = new TreeSet<>();
            for (String s : searchElement.getValue()) {
                s = s.toLowerCase();
                int k = -1;
                while((k = initialText.indexOf(s, k+1)) != -1) {
                    curIndexes.add(k);
                }
            }
            indexes.add(curIndexes);
        }
        return indexes;
    }

    private List<Integer> findClosestWordCombination(List<NavigableSet<Integer>> indexes)  {
        List<Integer> indexCombination = new ArrayList<>();
        NavigableSet<Integer> baseSet = indexes.remove(0);
        if (baseSet == null || baseSet.isEmpty()) {
            return new ArrayList<>();
        }
        for (Integer curIndex : baseSet) {
            List<Integer> curCombination = new ArrayList<>(List.of(curIndex));
            for (NavigableSet<Integer> curSet : indexes) {
                Integer closest = getClosestElement(curIndex, curSet.ceiling(curIndex), curSet.floor(curIndex));
                if (closest != null) {
                    curCombination.add(closest);
                }
            }
            if (indexCombination.isEmpty()) {
                indexCombination.addAll(curCombination);
            } else {
                if (getCombinationLength(curCombination) < getCombinationLength(indexCombination)) {
                    indexCombination = new ArrayList<>(curCombination);
                }
            }
        }
        return indexCombination.stream().sorted().toList();
    }

    private int getCombinationLength(List<Integer> combination) {
        return combination.stream().max(Integer::compareTo).orElse(Integer.MAX_VALUE) - combination.stream().min(Integer::compareTo).orElse(0);
    }

    private Integer getClosestElement(Integer baseEl, Integer el1, Integer el2) {
        if (el1 == null && el2 == null) {
            return null;
        } else if (el1 == null || el2 == null) {
            return el1 == null ? el2 : el1;
        } else {
            return Math.abs(el1 - baseEl) < Math.abs(el2 - baseEl) ? el1 : el2;
        }
    }

    private String boldSearchWords(List<Integer> indexCombination, String snippet) {
        for (int wordStartIndex : indexCombination) {
            int wordEndIndex = initialText.indexOf(" ", wordStartIndex+1);
            String curWordToBold = initialText.substring(wordStartIndex, wordEndIndex);
            snippet = snippet.replace(curWordToBold, MessageFormat.format("<b>{0}</b>", curWordToBold));
        }
        return snippet;
    }
}
