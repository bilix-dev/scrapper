package cl.bilix.scrapper.helpers;

import org.apache.commons.lang3.exception.ExceptionUtils;

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

    public WebScrapperResult(WebScrapperMessage webScrapperMessage, Throwable error) {
        this(webScrapperMessage);
        this.cause = ExceptionUtils.getStackTrace(error);
    }
}
