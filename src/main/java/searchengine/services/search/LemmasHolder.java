package searchengine.services.search;

import java.util.Set;

public interface LemmasHolder {

    void load();
    Set<Integer> filterLemmasIds();

}
