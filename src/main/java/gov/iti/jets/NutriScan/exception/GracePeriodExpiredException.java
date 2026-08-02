package gov.iti.jets.NutriScan.exception;

public class GracePeriodExpiredException extends RuntimeException {
    public GracePeriodExpiredException(String message) {
        super(message);
    }
}
