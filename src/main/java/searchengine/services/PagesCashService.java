package searchengine.services;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import searchengine.model.Page;
import searchengine.model.Site;
import searchengine.repository.PageRepository;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
@Getter
@RequiredArgsConstructor
public class PagesCashService {

//    private final PageRepository pageRepository;

    private final ConcurrentMap<Site, Page> webSitePages = new ConcurrentHashMap<>();

    public void addPage(Site domain, Page page) {
//        Page savedPage = pageRepository.save(page);
        webSitePages.put(domain, page);
    }

    public List<Page> getPagesList() {
        return webSitePages.values().stream().toList();
    }

    public void addAllPages(Site domain, Set<Page> pages) {
        pages.forEach(page -> webSitePages.put(domain, page));
    }
}
