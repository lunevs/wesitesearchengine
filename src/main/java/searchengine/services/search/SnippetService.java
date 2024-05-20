package searchengine.services.search;

public interface SnippetService {

    void init(String initialText);
    String buildSnippet();

}
