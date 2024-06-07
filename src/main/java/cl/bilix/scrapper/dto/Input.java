package cl.bilix.scrapper.dto;

import io.micrometer.common.lang.NonNull;
import lombok.Data;

@Data
public class Input {
    @NonNull
    private String terminal;
    @NonNull
    private String userName;
    @NonNull
    private String password;
    @NonNull
    private Payload payload;
    // Injectables
    private String url;
    private int timeout;
}
