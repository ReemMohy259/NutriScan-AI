package gov.iti.jets.NutriScan.exception;

public class UserConflictException extends ResourceAlreadyExistsException {
    public UserConflictException(String message) {
        super(message);
    }
}
