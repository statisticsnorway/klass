package no.ssb.klass.api.dto;

/**
 * @author Mads Lundemo, SSB.
 */
public class SubscribeResponse {

    public static final SubscribeResponse CREATED = new SubscribeResponse("STATUS_CREATED",
            "Subscription created.\nAn email is sent for verification.");
    public static final SubscribeResponse DELETED = new SubscribeResponse("STATUS_DELETED", "Subscription deleted.");
    public static final SubscribeResponse EXISTS = new SubscribeResponse("STATUS_EXISTS",
            "Email already subscribed to classification");
    public static final SubscribeResponse EMAIL_PROBLEM = new SubscribeResponse("STATUS_EMAIL_ERROR",
            "A problem occurred while sending verification email");
    public static final SubscribeResponse UNKNOWN_ERROR = new SubscribeResponse("STATUS_UNKNOWN_ERROR",
            "Unknown error occurred");

    private final String code;
    private final String message;

    public SubscribeResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
