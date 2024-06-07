package cl.bilix.scrapper.properties;

import org.springframework.format.annotation.NumberFormat;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Validated
public class Properties {
    @NotNull
    private String url;
    // @NotNull
    // private String username;
    // @NotNull
    // private String password;
    @NumberFormat
    @NotNull
    private int timeout;
    private String map;
}
