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
    private String tariffCode;

    // TRANSPORTISTA
    private String gd;
    private String micdta;
    private String seal;
    private String dni;
    private String plateNumber;
    private String country;
    private String plateNumberCountry;

    // CONTENEDOR
    private String dispatcher;
    private String clientRut;
    private String weight;
    private String vgmWeight;
    private String shippingCompany;
    private String businessName;
    private String operation;
    private String ship;
    private String custom;
    private String containerType;
    private String vgmWeightVerifier;
    private String weightChargeOnly;
    private String isoCode;
    private String numCartaPorte;
    private String consignee;
    private String sealLine;
    private String choferName;

    private boolean foreign;
}
