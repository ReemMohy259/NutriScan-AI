package gov.iti.jets.NutriScan.exception;

public class AccountNotPendingDeletionException extends RuntimeException {
    public AccountNotPendingDeletionException(String message) {
        super(message);
    }
}
