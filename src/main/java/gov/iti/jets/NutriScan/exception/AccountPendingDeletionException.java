package gov.iti.jets.NutriScan.exception;

public class AccountPendingDeletionException extends RuntimeException {
    public AccountPendingDeletionException(String message) {
        super(message);
    }
}
