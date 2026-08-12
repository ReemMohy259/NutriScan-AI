package gov.iti.jets.NutriScan.exception;

import gov.iti.jets.NutriScan.dto.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.core.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationExceptions(
        MethodArgumentNotValidException ex,
        HttpServletRequest request) {

        List<ApiErrorResponse.ErrorDetail> details = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(
                error -> ApiErrorResponse.ErrorDetail.builder()
                    .field(error.getField())
                    .issue(error.getDefaultMessage())
                    .build())
            .collect(Collectors.toList());

        ApiErrorResponse response = ApiErrorResponse.builder()
            .timestamp(Instant.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error("VALIDATION_ERROR")
            .message("Validation failed")
            .details(details)
            .path(request.getRequestURL().toString())
            .build();

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ApiErrorResponse> handleHandlerMethodValidationException(
        HandlerMethodValidationException ex,
        HttpServletRequest request) {

        List<ApiErrorResponse.ErrorDetail> details = ex.getParameterValidationResults()
            .stream()
            .flatMap(
                result -> result.getResolvableErrors()
                    .stream()
                    .map(
                        error -> ApiErrorResponse.ErrorDetail.builder()
                            .field(result.getMethodParameter().getParameterName())
                            .issue(error.getDefaultMessage())
                            .build()))
            .toList();

        ApiErrorResponse response = ApiErrorResponse.builder()
            .timestamp(Instant.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error("VALIDATION_ERROR")
            .message("Validation failed")
            .details(details)
            .path(request.getRequestURL().toString())
            .build();

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(
        ResourceNotFoundException ex,
        HttpServletRequest request) {

        ApiErrorResponse response = ApiErrorResponse.builder()
            .timestamp(Instant.now())
            .status(HttpStatus.NOT_FOUND.value())
            .error("NOT_FOUND")
            .message(ex.getMessage())
            .path(request.getRequestURL().toString())
            .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleConflict(
        ResourceAlreadyExistsException ex,
        HttpServletRequest request) {

        ApiErrorResponse response = ApiErrorResponse.builder()
            .timestamp(Instant.now())
            .status(HttpStatus.CONFLICT.value())
            .error("ALREADY_EXISTS")
            .message(ex.getMessage())
            .path(request.getRequestURL().toString())
            .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiErrorResponse> handleBusiness(
        BusinessException ex,
        HttpServletRequest request) {

        ApiErrorResponse response = ApiErrorResponse.builder()
            .timestamp(Instant.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error("BUSINESS_ERROR")
            .message(ex.getMessage())
            .path(request.getRequestURL().toString())
            .build();

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(UserAlreadyVerifiedException.class)
    public ResponseEntity<ApiErrorResponse> handleUserAlreadyVerified(
        UserAlreadyVerifiedException ex,
        HttpServletRequest request) {

        ApiErrorResponse response = ApiErrorResponse.builder()
            .timestamp(Instant.now())
            .status(HttpStatus.CONFLICT.value())
            .error("USER_ALREADY_VERIFIED")
            .message(ex.getMessage())
            .path(request.getRequestURL().toString())
            .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler({IllegalArgumentException.class, PropertyReferenceException.class,
            InvalidDataAccessApiUsageException.class})
    public ResponseEntity<ApiErrorResponse> handleIllegalArgumentException(
        Exception ex,
        HttpServletRequest request) {

        ApiErrorResponse response = ApiErrorResponse.builder()
            .timestamp(Instant.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error("BAD_REQUEST")
            .message(ex.getMessage())
            .path(request.getRequestURL().toString())
            .build();

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(OcrModelException.class)
    public ResponseEntity<ApiErrorResponse> handleOcrModelException(
        Exception ex,
        HttpServletRequest request) {

        ex.printStackTrace();

        ApiErrorResponse response = ApiErrorResponse.builder()
            .timestamp(Instant.now())
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .error("INTERNAL_SERVER_ERROR")
            .message(ex.getMessage())
            .path(request.getRequestURL().toString())
            .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
    @ExceptionHandler(IngredientParsingException.class)
    public ResponseEntity<String> handleIngredientParsing(IngredientParsingException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).body(ex.getMessage());
    }

    @ExceptionHandler(ImageTooLargeException.class)
    public ResponseEntity<ApiErrorResponse> handleImageTooLargeException(
        ImageTooLargeException ex,
        HttpServletRequest request) {

        ApiErrorResponse response = ApiErrorResponse.builder()
            .timestamp(Instant.now())
            .status(HttpStatus.CONTENT_TOO_LARGE.value())
            .error("IMAGE_TOO_LARGE")
            .message(ex.getMessage())
            .path(request.getRequestURL().toString())
            .build();

        return ResponseEntity.status(HttpStatus.CONTENT_TOO_LARGE).body(response);
    }
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiErrorResponse> maxUploadSizeExceededException(
        MaxUploadSizeExceededException ex,
        HttpServletRequest request) {

        ApiErrorResponse response = ApiErrorResponse.builder()
            .timestamp(Instant.now())
            .status(HttpStatus.CONTENT_TOO_LARGE.value())
            .error("CONTENT_TOO_LARGE")
            .message(ex.getMessage())
            .path(request.getRequestURL().toString())
            .build();

        return ResponseEntity.status(HttpStatus.CONTENT_TOO_LARGE).body(response);
    }

    @ExceptionHandler(NoImageProvidedException.class)
    public ResponseEntity<ApiErrorResponse> handleNoImageProvidedException(
        NoImageProvidedException ex,
        HttpServletRequest request) {

        ApiErrorResponse response = ApiErrorResponse.builder()
            .timestamp(Instant.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error("NO_IMAGE_PROVIDED")
            .message(ex.getMessage())
            .path(request.getRequestURL().toString())
            .build();

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(InvalidImageException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidImageException(
        InvalidImageException ex,
        HttpServletRequest request) {

        ApiErrorResponse response = ApiErrorResponse.builder()
            .timestamp(Instant.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error("INVALID_IMAGE")
            .message(ex.getMessage())
            .path(request.getRequestURL().toString())
            .build();

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(ImageUploadException.class)
    public ResponseEntity<ApiErrorResponse> handleImageUploadException(
        ImageUploadException ex,
        HttpServletRequest request) {

        ApiErrorResponse response = ApiErrorResponse.builder()
            .timestamp(Instant.now())
            .status(HttpStatus.SERVICE_UNAVAILABLE.value())
            .error("IMAGE_UPLOAD_ERROR")
            .message(ex.getMessage())
            .path(request.getRequestURL().toString())
            .build();

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    @ExceptionHandler(KeyCloakCreationException.class)
    public ResponseEntity<ApiErrorResponse> handleKeycloakCreationException(
        KeyCloakCreationException ex,
        HttpServletRequest request) {

        ApiErrorResponse response = ApiErrorResponse.builder()
            .timestamp(Instant.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error("KEYCLOAK_CREATION_FAILED")
            .message(ex.getMessage())
            .path(request.getRequestURL().toString())
            .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(ImageTooBlurry.class)
    public ResponseEntity<ApiErrorResponse> handleImageTooBlurry(
        ImageTooBlurry ex,
        HttpServletRequest request) {

        ApiErrorResponse response = ApiErrorResponse.builder()
            .timestamp(Instant.now())
            .status(HttpStatus.UNPROCESSABLE_CONTENT.value())
            .error("IMAGE_TOO_BLURRY")
            .message(ex.getMessage())
            .path(request.getRequestURL().toString())
            .build();

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGlobalException(
        Exception ex,
        HttpServletRequest request) {

        ex.printStackTrace();

        ApiErrorResponse response = ApiErrorResponse.builder()
            .timestamp(Instant.now())
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .error("INTERNAL_SERVER_ERROR")
            .message(ex.getMessage())
            .path(request.getRequestURL().toString())
            .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(AccountPendingDeletionException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidImageException(
        AccountPendingDeletionException ex,
        HttpServletRequest request) {

        ApiErrorResponse response = ApiErrorResponse.builder()
            .timestamp(Instant.now())
            .status(HttpStatus.CONFLICT.value())
            .error("ACCOUNT_PENDING_DELETION")
            .message(ex.getMessage())
            .path(request.getRequestURL().toString())
            .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidImageException(
        IllegalStateException ex,
        HttpServletRequest request) {

        ApiErrorResponse response = ApiErrorResponse.builder()
            .timestamp(Instant.now())
            .status(HttpStatus.CONFLICT.value())
            .error("ILLEGAL_STATE")
            .message(ex.getMessage())
            .path(request.getRequestURL().toString())
            .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handleTypeMismatchExceptions(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request) {

        String issue = "Invalid value: " + ex.getValue();

        if (ex.getRequiredType() != null && ex.getRequiredType().isEnum()) {
            String allowedValues = Arrays.stream(ex.getRequiredType().getEnumConstants())
                    .map(Object::toString)
                    .collect(Collectors.joining(", "));

            issue = String.format(
                    "Invalid value '%s'. Allowed values: %s",
                    ex.getValue(),
                    allowedValues
            );
        }

        List<ApiErrorResponse.ErrorDetail> details = List.of(
                ApiErrorResponse.ErrorDetail.builder()
                        .field(ex.getName())
                        .issue(issue)
                        .build()
        );

        ApiErrorResponse response = ApiErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("VALIDATION_ERROR")
                .message("Invalid request parameter")
                .details(details)
                .path(request.getRequestURL().toString())
                .build();

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(AccountNotPendingDeletionException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidImageException(
        AccountNotPendingDeletionException ex,
        HttpServletRequest request) {

        ApiErrorResponse response = ApiErrorResponse.builder()
            .timestamp(Instant.now())
            .status(HttpStatus.CONFLICT.value())
            .error("ACCOUNT_IS_NOT_PENDING_DELETION")
            .message(ex.getMessage())
            .path(request.getRequestURL().toString())
            .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(GracePeriodExpiredException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidImageException(
        GracePeriodExpiredException ex,
        HttpServletRequest request) {

        ApiErrorResponse response = ApiErrorResponse.builder()
            .timestamp(Instant.now())
            .status(HttpStatus.CONFLICT.value())
            .error("ACCOUNT_DELETION_FINALIZED")
            .message(ex.getMessage())
            .path(request.getRequestURL().toString())
            .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }
}