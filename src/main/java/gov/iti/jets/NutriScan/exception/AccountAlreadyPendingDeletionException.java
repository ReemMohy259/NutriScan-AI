package gov.iti.jets.NutriScan.exception;

public class AccountAlreadyPendingDeletionException extends RuntimeException {
    public AccountAlreadyPendingDeletionException(String message) {
        super(message);
    }
}
