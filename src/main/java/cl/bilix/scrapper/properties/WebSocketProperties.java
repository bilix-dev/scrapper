package cl.bilix.scrapper.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@ConfigurationProperties("ws")
@Configuration
@Data
@Validated
public class WebSocketProperties {
    @NotNull
    private String[] origins;
}
