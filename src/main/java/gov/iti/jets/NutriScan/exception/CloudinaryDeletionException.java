package gov.iti.jets.NutriScan.exception;

public class CloudinaryDeletionException extends RuntimeException {
    public CloudinaryDeletionException(String imageUrl, String message) {
        super(message);
    }
}
