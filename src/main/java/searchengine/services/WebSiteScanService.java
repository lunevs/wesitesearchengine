package searchengine.services;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import searchengine.model.Page;
import searchengine.model.Site;
import searchengine.model.WebSiteNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.RecursiveTask;
import java.util.logging.Logger;

@Getter
@RequiredArgsConstructor
public class WebSiteScanService extends RecursiveTask<Set<String>> {

    private final Site webSite;
    private final String currentPath;
    private final PagesCashService cashService;

    private final Logger logger = Logger.getLogger(WebSiteScanService.class.getName());

    @Override
    protected Set<String> compute() {
        String domainUrl = webSite.getSiteUrl();
        Set<String> resultPaths = new HashSet<>();
        List<WebSiteScanService> tasks = new ArrayList<>();

//        long t0 = System.currentTimeMillis();
        Document doc;
        Connection connection;
        try {
            connection = Jsoup.connect(domainUrl + currentPath);
            doc = connection.get();
            Page currentPage = new Page()
                    .setPageContent(doc.html())
                    .setSite(webSite)
                    .setResponseCode(connection.response().statusCode())
                    .setPagePath(currentPath);
            logger.info("Add page: " + webSite.getSiteUrl() + " - " + currentPath);
            cashService.addPage(webSite, currentPage);
            resultPaths.add(currentPath);
            Thread.sleep(200);
        } catch (IOException | InterruptedException e) {
            System.out.println("Can't read url: " + (domainUrl + currentPath) + ". Error: " + e.getMessage());
            return resultPaths;
        }
        Elements elements = doc.getElementsByTag("a");
//        long t1 = System.currentTimeMillis() - t0;
        Set<String> currentLinks = new HashSet<>();
        for (Element element : elements) {
            String currentUrl = element.attr("href");
            if (currentUrl.startsWith(domainUrl)) {
                currentUrl = currentUrl.substring(domainUrl.length());
            }
//            if (currentUrl.matches("^(\\/[a-zA-Z\\-\\sа-яА-Я\\_]+)+\\/$") && currentUrl.startsWith(currentPath) && !currentUrl.equals(currentPath)) {
            if (currentUrl.startsWith(currentPath) && !currentUrl.equals(currentPath)) {
                currentLinks.add(currentUrl);
            }
        }
//        long t2 = System.currentTimeMillis() - t0;
        //logger.info(Thread.currentThread().getName() + "\t" + currentPath + "\tfound links: " + currentLinks.size() + "\ttime to getUrl: " + t1 + "\ttime to parsePage: " + (t2-t1));

        if (!currentLinks.isEmpty()) {
            currentLinks.forEach(link -> {
                WebSiteScanService task = new WebSiteScanService(webSite, link, cashService);
                task.fork();
                tasks.add(task);
            });
        }
        for (WebSiteScanService task : tasks) {
            resultPaths.addAll(task.join());
        }
        return resultPaths;
    }

}
