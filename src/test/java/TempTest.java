import org.apache.lucene.morphology.LuceneMorphology;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import searchengine.config.AppConfiguration;

import java.util.List;
import java.util.Set;

@SpringBootTest(classes = AppConfiguration.class)
public class TempTest {

    @Autowired
    private LuceneMorphology luceneMorphology;

    @Test
    public void checkOneWord() {

        String word = "темно-синий";
        List<String> res = luceneMorphology.getNormalForms(word);

        System.out.println(res);

    }

}
