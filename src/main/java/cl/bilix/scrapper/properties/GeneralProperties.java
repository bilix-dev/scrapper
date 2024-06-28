package cl.bilix.scrapper.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@ConfigurationProperties("general")
@Configuration
@Data
@Validated
public class GeneralProperties {
    @NotNull
    private boolean headless;
}
