package cl.bilix.scrapper.helpers;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
public class WebScrapperResult {
    private Object code;
    private Object message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Object cause;

    public WebScrapperResult(WebScrapperMessage webScrapperMessage) {
        this.code = webScrapperMessage.getCode();
        this.message = webScrapperMessage.getMessage();
    }

    public WebScrapperResult(WebScrapperMessage webScrapperMessage, String cause) {
        this(webScrapperMessage);
        this.cause = cause;
    }
}
