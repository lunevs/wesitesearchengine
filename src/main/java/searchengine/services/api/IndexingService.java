package searchengine.services.api;

import searchengine.data.dto.api.DefaultResponse;

public interface IndexingService {

    DefaultResponse startIndexing();
    DefaultResponse stopIndexing();
    DefaultResponse indexPage(String url);


}
