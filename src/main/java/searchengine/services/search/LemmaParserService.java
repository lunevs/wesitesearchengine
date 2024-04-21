package searchengine.services.search;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.WrongCharaterException;
import org.springframework.stereotype.Service;
import searchengine.data.dto.LemmaDto;
import searchengine.data.dto.SearchIndexDto;
import searchengine.data.repository.JdbcLemmaRepository;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class LemmaParserService {

    private final LuceneMorphology luceneMorphology;
    private final JdbcLemmaRepository lemmaRepository;
    private final SearchIndexService searchIndexService;

    private static final String WORD_TYPE_REGEX = "\\W\\w&&[^а-яА-Я\\s]";
    private static final String[] particlesNames = new String[]{"МЕЖД", "ПРЕДЛ", "СОЮЗ"};

    private static final int MIN_WORD_LENGTH = 3;

    public void parseAndSaveAllLemmas(String page, int siteId, int pageId) {
        Map<String, Integer> lemmas = collectLemmas(page);
        log.info("page: {} lemmas: {}", pageId, lemmas.keySet());
        saveLemmas(lemmas, siteId);
        List<LemmaDto> savedLemmas = getAllByNames(lemmas.keySet(), siteId);
        List<SearchIndexDto> searchIndexDtos = savedLemmas.stream()
                .map(el -> new SearchIndexDto()
                                .setPageId(pageId)
                                .setLemmaId(el.getId())
                                .setLemmaRank(Float.valueOf(lemmas.get(el.getLemma())))
                        )
                .toList();
        searchIndexService.saveAll(searchIndexDtos);
        log.info(Thread.currentThread().getName() + " saved " + searchIndexDtos.size() + " lemmas for site: " + siteId);

    }

    public void deleteAllLemmasForSite(int siteId) {
        lemmaRepository.deleteAllForSite(siteId);
    }

    public List<LemmaDto> getAllByNames(Set<String> names, int siteId) {
        return lemmaRepository.getAllByNames(names, siteId);
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

    public Map<String, Set<String>> mapLemmasToText(String text) {
        String[] words = arrayContainsRussianWords(text);
        HashMap<String, Set<String>> lemmas = new HashMap<>();

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
            Set<String> tmpSet = new HashSet<>();
            if (lemmas.containsKey(normalWord)) {
                tmpSet = lemmas.get(normalWord);
            }
            tmpSet.add(word);
            lemmas.put(normalWord, tmpSet);
        }
        return lemmas;
    }


    public void saveLemmas(Map<String, Integer> lemmas, int siteId) {
        List<LemmaDto> lemmaDtoList = lemmas.entrySet().stream()
                .map(el -> new LemmaDto()
                        .setLemma(el.getKey())
                        .setFrequency(el.getValue())
                        .setSiteId(siteId))
                .toList();
        lemmaRepository.saveAll(lemmaDtoList);
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


    /**
     * @param text текст из которого собираем все леммы
     * @return набор уникальных лемм найденных в тексте
     */
    public Set<String> getLemmaSet(String text) {
        log.info("input string: {}", text);
        String[] textArray = arrayContainsRussianWords(text);
        log.info("result parsed array: {}", Arrays.toString(textArray));
        Set<String> lemmaSet = new HashSet<>();
        for (String word : textArray) {
            if (!word.isEmpty() && isCorrectWordForm(word)) {
                List<String> wordBaseForms = luceneMorphology.getMorphInfo(word);
                log.info("word: {} - base forms: {}", word, wordBaseForms);
                if (anyWordBaseBelongToParticle(wordBaseForms)) {
                    continue;
                }
                lemmaSet.addAll(luceneMorphology.getNormalForms(word));
            }
        }
        return lemmaSet;
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

    private boolean isCorrectWordForm(String word) {
        List<String> wordInfo = luceneMorphology.getMorphInfo(word);
        for (String morphInfo : wordInfo) {
            if (morphInfo.matches(WORD_TYPE_REGEX)) {
                return false;
            }
        }
        return true;
    }
}
