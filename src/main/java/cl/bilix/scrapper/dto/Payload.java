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

    // TRANSPORTISTA
    @NotNull
    private String gd;
    @NotNull
    private String micdta;
    @NotNull
    private String seal;

    // CONTENEDOR
    private String dispatcherRut;
    private String clientRut;
    private String weight;
    private String vgmWeight;
    private String shippingCompany;
    private String businessName;
    private String operation;
}
