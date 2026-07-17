package gov.iti.jets.NutriScan.exception;

public class AllergyConflictException extends ResourceAlreadyExistsException {
    public AllergyConflictException(String message) {
        super(message);
    }
}
