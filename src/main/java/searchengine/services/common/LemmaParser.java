package searchengine.services.common;

import java.util.Map;

public interface LemmaParser {

    void parseLemmasFromPage(String pageBodyText, int siteId, int pageId);
    Map<String, Integer> collectLemmas(String text);
    String getNormalWordForm(String word);
}
