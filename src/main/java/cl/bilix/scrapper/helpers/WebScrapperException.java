package cl.bilix.scrapper.helpers;

public class WebScrapperException extends RuntimeException {

    private final WebScrapperMessage message;

    public WebScrapperResult getErrorResult() {
        return new WebScrapperResult(message,
                this);
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
