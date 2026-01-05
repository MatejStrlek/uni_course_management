package hr.algebra.uni_course_management.exception;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice(basePackages = "hr.algebra.uni_course_management.controller.api")
@Order(1)
public class RestExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGlobalException(Exception ex, WebRequest request) {
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("timestamp", LocalDateTime.now());
        error.put("message", "Internal server error: " + ex.getMessage());
        error.put("path", request.getDescription(false).replace("uri=", ""));
        error.put("data", null);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntimeException(RuntimeException ex, WebRequest request) {
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("timestamp", LocalDateTime.now());
        error.put("message", ex.getMessage());
        error.put("path", request.getDescription(false).replace("uri=", ""));
        error.put("data", null);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, Object> error = new HashMap<>();
        Map<String, String> fieldErrors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(fieldError ->
                fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage())
        );

        error.put("success", false);
        error.put("timestamp", LocalDateTime.now());
        error.put("message", "Validation failed");
        error.put("path", request.getDescription(false).replace("uri=", ""));
        error.put("errors", fieldErrors);
        error.put("data", null);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("timestamp", LocalDateTime.now());
        error.put("message", "Access denied: You don't have permission to access this resource");
        error.put("path", request.getDescription(false).replace("uri=", ""));
        error.put("data", null);

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleBadCredentialsException(BadCredentialsException ex, WebRequest request) {
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("timestamp", LocalDateTime.now());
        error.put("message", "Invalid username or password");
        error.put("path", request.getDescription(false).replace("uri=", ""));
        error.put("data", null);

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("timestamp", LocalDateTime.now());
        error.put("message", ex.getMessage());
        error.put("path", request.getDescription(false).replace("uri=", ""));
        error.put("data", null);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("timestamp", LocalDateTime.now());
        error.put("message", ex.getMessage());
        error.put("path", request.getDescription(false).replace("uri=", ""));
        error.put("data", null);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
}