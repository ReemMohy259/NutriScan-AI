package gov.iti.jets.NutriScan.exception;

public class OcrModelException extends RuntimeException {
    public OcrModelException(String message, Exception e) {
        super(message, e);
    }
    public OcrModelException(String message) {
        super(message);
    }
}
