package cl.bilix.scrapper.helpers;

public enum WebScrapperMessage {

    SUCCESS(0, "Procedimiento realizado con exito"),
    UNAUTHORIZED(1, "Credenciales incorrectas"),
    UNINMPLEMENTED(2, "Terminal no implementado"),
    ERROR(3, "Error general");

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