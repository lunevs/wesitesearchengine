package searchengine.data.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class WebSiteNode implements Comparable<WebSiteNode> {
    private String url;
    private Integer level;
    private WebSiteNode parentNode;

    @Override
    public int compareTo(WebSiteNode o) {
        return url.compareTo(o.getUrl());
    }
}
