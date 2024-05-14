package searchengine.data.dto.api;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DefaultResponse {

    private boolean result;
    private String error;
}
