package gov.iti.jets.NutriScan.exception;

public class NoImageProvidedException extends ResourceNotFoundException {
    public NoImageProvidedException(String message) {
        super(message);
    }
}
