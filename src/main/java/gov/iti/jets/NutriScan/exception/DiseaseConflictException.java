package gov.iti.jets.NutriScan.exception;

public class DiseaseConflictException extends ResourceAlreadyExistsException {
    public DiseaseConflictException(String message) {
        super(message);
    }
}
