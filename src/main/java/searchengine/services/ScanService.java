package searchengine.services;

import lombok.Getter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import searchengine.model.WebSiteNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.RecursiveTask;

@Getter
public class ScanService extends RecursiveTask<List<WebSiteNode>> {

    private final String domainUrl;
    private final WebSiteNode node;

    public ScanService(WebSiteNode node, String domainUrl) {
        this.node = node;
        this.domainUrl = domainUrl;
    }


    @Override
    protected List<WebSiteNode> compute() {

        List<WebSiteNode> totalNodes = new ArrayList<>();
        List<ScanService> tasks = new ArrayList<>();

        long start = System.currentTimeMillis();
        Document doc = null;
        try {
            doc = Jsoup.connect(domainUrl + node.getUrl()).get();
            Thread.sleep(100);
        } catch (IOException | InterruptedException e) {
            System.out.println("Can't read url: " + (domainUrl + node.getUrl()) + ". Error: " + e.getMessage());
            return totalNodes;
        }
        String parentUrl = node.getUrl();
        int nextLevel = node.getLevel() + 1;
        Elements elements = doc.getElementsByTag("a");
        Set<String> currentLinks = getLinks(elements, parentUrl);

        System.out.println(parentUrl + "\tfound: " + currentLinks.size() + "\ttime: " + (System.currentTimeMillis() - start));

        if (!currentLinks.isEmpty()) {
            currentLinks.forEach(link -> {
                WebSiteNode currentNode = new WebSiteNode(link, nextLevel, node);
                ScanService task = new ScanService(currentNode, domainUrl);
                totalNodes.add(currentNode);
                task.fork();
                tasks.add(task);
            });
        }
        for (ScanService task : tasks) {
            totalNodes.addAll(task.join());
        }
        return totalNodes;
    }

    private Set<String> getLinks(Elements elements, String parentUrl) {
        Set<String> links = new TreeSet<>();
        for (Element element : elements) {
            String currentUrl = element.attr("href");
            if (currentUrl.matches("^(\\/[a-zA-Z\\-\\sа-яА-Я\\_]+)+\\/$") && currentUrl.startsWith(parentUrl) && !currentUrl.equals(parentUrl)) {
                links.add(currentUrl);
            }
        }
        return links;
    }
}
