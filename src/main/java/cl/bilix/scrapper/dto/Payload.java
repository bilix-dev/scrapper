package cl.bilix.scrapper.dto;

import io.micrometer.common.lang.NonNull;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class Payload {
    @NonNull
    private String id;
    @NotNull
    private String container;
    @NotNull
    private String booking;
}
