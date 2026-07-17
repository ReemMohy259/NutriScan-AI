package gov.iti.jets.NutriScan.exception;

import gov.iti.jets.NutriScan.dto.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.time.Instant;
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

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleEmailExists(
        EmailAlreadyExistsException ex,
        HttpServletRequest request) {

        ApiErrorResponse response = ApiErrorResponse.builder()
            .timestamp(Instant.now())
            .status(HttpStatus.CONFLICT.value())
            .error("CONFLICT")
            .message(ex.getMessage())
            .path(request.getRequestURL().toString())
            .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    // @ExceptionHandler(AccessDeniedException.class)
    // public ResponseEntity<ApiErrorResponse> handleAccessDenied(
    // AccessDeniedException ex,
    // HttpServletRequest request) {
    //
    // ApiErrorResponse response = ApiErrorResponse.builder()
    // .timestamp(Instant.now())
    // .status(HttpStatus.FORBIDDEN.value())
    // .error("FORBIDDEN")
    // .message(ex.getMessage())
    // .path(request.getRequestURL().toString())
    // .build();
    //
    // return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    // }

    // @ExceptionHandler(BadCredentialsException.class)
    // public ResponseEntity<ApiErrorResponse> handleBadCredentials(
    // BadCredentialsException ex,
    // HttpServletRequest request) {
    //
    // ApiErrorResponse response = ApiErrorResponse.builder()
    // .timestamp(Instant.now())
    // .status(HttpStatus.UNAUTHORIZED.value())
    // .error("UNAUTHORIZED")
    // .message("Invalid email or password")
    // .path(request.getRequestURL().toString())
    // .build();
    //
    // return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    // }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgumentException(
        IllegalArgumentException ex,
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

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGlobalException(
        Exception ex,
        HttpServletRequest request) {

        ex.printStackTrace();

        ApiErrorResponse response = ApiErrorResponse.builder()
            .timestamp(Instant.now())
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .error("INTERNAL_SERVER_ERROR")
            .message("An unexpected error occurred.")
            .path(request.getRequestURL().toString())
            .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}