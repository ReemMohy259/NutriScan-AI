package gov.iti.jets.NutriScan.exception;

public class EmailNotFoundException extends ResourceNotFoundException {
    public EmailNotFoundException(String message) {
        super("email not found: " + message);
    }
}
