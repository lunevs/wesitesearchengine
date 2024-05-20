package searchengine.services.common;

import lombok.RequiredArgsConstructor;
import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.WrongCharaterException;
import searchengine.data.dto.common.LemmaDto;
import searchengine.services.scanner.SearchIndexService;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@RequiredArgsConstructor
public class LemmaParserImpl implements LemmaParser {

    private final LuceneMorphology luceneMorphology;
    private final SearchIndexService searchIndexService;
    private final LemmaService lemmaService;

    private static final String[] particlesNames = new String[]{"МЕЖД", "ПРЕДЛ", "СОЮЗ"};

    private static final int MIN_WORD_LENGTH = 3;

    public void parseLemmasFromPage(String pageBodyText, int siteId, int pageId) {
        Map<String, Integer> lemmas = collectLemmas(pageBodyText);
        lemmaService.saveLemmas(lemmas, siteId);
        List<LemmaDto> savedLemmas = lemmaService.getAllByNames(lemmas.keySet(), siteId);
        searchIndexService.saveAll(savedLemmas, pageId, lemmas);
    }

    /**
     * Метод разделяет текст на слова, находит все леммы и считает их количество.
     *
     * @param text текст из которого будут выбираться леммы
     * @return ключ является леммой, а значение количеством найденных лемм
     */
    public Map<String, Integer> collectLemmas(String text) {
        String[] words = arrayContainsRussianWords(text);
        HashMap<String, Integer> lemmas = new HashMap<>();

        for (String word : words) {
            if (word.isBlank() || word.length() < MIN_WORD_LENGTH) {
                continue;
            }

            List<String> wordBaseForms = luceneMorphology.getMorphInfo(word);
            if (anyWordBaseBelongToParticle(wordBaseForms)) {
                continue;
            }

            List<String> normalForms = luceneMorphology.getNormalForms(word);
            if (normalForms.isEmpty()) {
                continue;
            }

            String normalWord = normalForms.get(0);
            if (lemmas.containsKey(normalWord)) {
                lemmas.put(normalWord, lemmas.get(normalWord) + 1);
            } else {
                lemmas.put(normalWord, 1);
            }
        }
        return lemmas;
    }

    public String getNormalWordForm(String word) {
        String russianWord = word.toLowerCase(Locale.ROOT).replaceAll("([^а-я\\s])", " ").trim();
        if (russianWord.length() < 3) {
            return "";
        }
        try {
            List<String> normalForms = luceneMorphology.getNormalForms(russianWord);
            return normalForms.isEmpty() ? "" : normalForms.get(0);
        } catch (WrongCharaterException e) {
            return "";
        }
   }

    private boolean anyWordBaseBelongToParticle(List<String> wordBaseForms) {
        return wordBaseForms.stream().anyMatch(this::hasParticleProperty);
    }

    private boolean hasParticleProperty(String wordBase) {
        for (String property : particlesNames) {
            if (wordBase.toUpperCase().contains(property)) {
                return true;
            }
        }
        return false;
    }

    private String[] arrayContainsRussianWords(String text) {
        return text.toLowerCase(Locale.ROOT)
                .replaceAll("([^а-я\\s])", " ")
                .trim()
                .split("\\s+");
    }

}
