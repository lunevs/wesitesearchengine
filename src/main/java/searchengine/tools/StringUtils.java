package searchengine.tools;

import java.util.ArrayList;
import java.util.List;

public class StringUtils {

    public static String createSnippet(String text, List<Integer> indexCombination) {
        List<String> snippetPieces = new ArrayList<>(indexCombination.size());
        int startIndex = 0;
        int endIndex = 0;
        for (int curIndex : indexCombination) {
            if (endIndex == 0) {
                startIndex = shiftLeft(text, curIndex);
                endIndex = shiftRight(text, curIndex);
                continue;
            }
            if (curIndex - endIndex > 100) {
                snippetPieces.add(text.substring(startIndex, endIndex));
                startIndex = shiftLeft(text, curIndex);
            }
            endIndex = shiftRight(text, curIndex);
        }
        snippetPieces.add(text.substring(startIndex, endIndex));
        return String.join(" ... ", snippetPieces);
    }

    private static int shiftLeft(String text, int curIndex) {
        int tmpIndex = text.indexOf(" ", curIndex - 50);
        return (tmpIndex == -1 || tmpIndex > curIndex) ? curIndex : tmpIndex;
    }

    private static int shiftRight(String text, int curIndex) {
        int tmpIndex = text.indexOf(" ", curIndex + 30);
        return (tmpIndex == -1) ? text.length() : tmpIndex;
    }

}
