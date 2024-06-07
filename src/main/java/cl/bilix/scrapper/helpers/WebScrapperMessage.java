package cl.bilix.scrapper.helpers;

public enum WebScrapperMessage {

    SUCCESS(0, "Procedimiento realizado con exito"),
    ERROR(1, "Error general"),
    UNAUTHORIZED(2, "Credenciales incorrectas"),
    UNINMPLEMENTED(3, "Terminal no implementado"),
    PROCCESSING(4, "Contenedor se esta procesando");

    private String message;
    private int code;

    WebScrapperMessage(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}