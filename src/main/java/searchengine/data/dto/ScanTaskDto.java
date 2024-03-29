package searchengine.data.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScanTaskDto {
    String url;
    String path;
    Integer siteId;

    public String getFullUrl() {
        return url.concat(path);
    }
}
