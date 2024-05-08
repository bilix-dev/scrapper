package cl.bilix.scrapper.helpers;

import org.springframework.util.ObjectUtils;

public class WebScrapperException extends RuntimeException {

    private final WebScrapperMessage message;

    public WebScrapperResult getErrorResult() {
        return new WebScrapperResult(message,
                !ObjectUtils.isEmpty(this.getCause()) ? this.getCause().getMessage() : null);
    }

    public WebScrapperException(WebScrapperMessage webScrapperMessage) {
        super(webScrapperMessage.getMessage());
        message = webScrapperMessage;
    }

    public WebScrapperException(WebScrapperMessage webScrapperMessage, Throwable err) {
        super(webScrapperMessage.getMessage(), err);
        message = webScrapperMessage;
    }
}
