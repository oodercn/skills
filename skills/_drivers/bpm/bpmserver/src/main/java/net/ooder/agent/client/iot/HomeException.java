package net.ooder.agent.client.iot;

public class HomeException extends Exception {
    private static final long serialVersionUID = 1L;

    public HomeException(String message) {
        super(message);
    }

    public HomeException(String message, Throwable cause) {
        super(message, cause);
    }
}
