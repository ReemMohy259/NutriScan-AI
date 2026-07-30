package gov.iti.jets.NutriScan.exception;

public class BedrockGatewayException extends RuntimeException {

    public BedrockGatewayException(String message) {
        super(message);
    }

    public BedrockGatewayException(String message, Throwable cause) {
        super(message, cause);
    }
}
