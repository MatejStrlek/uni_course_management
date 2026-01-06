package hr.algebra.uni_course_management.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RestExceptionHandlerTest {
    private final RestExceptionHandler handler = new RestExceptionHandler();

    @Test
    void handleGlobalException_Returns500WithMessage() {
        Exception ex = new Exception("boom");
        WebRequest request = mock(WebRequest.class);
        when(request.getDescription(false)).thenReturn("uri=/api/test");

        ResponseEntity<?> response = handler.handleGlobalException(ex, request);

        assertThat(response.getStatusCode().value()).isEqualTo(500);
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertThat(body.get("success")).isEqualTo(false);
        assertThat((String) body.get("message")).contains("Internal server error: boom");
        assertThat(body.get("path")).isEqualTo("/api/test");
    }

    @Test
    void handleRuntimeException_Returns400WithExceptionMessage() {
        RuntimeException ex = new RuntimeException("runtime issue");
        WebRequest request = mock(WebRequest.class);
        when(request.getDescription(false)).thenReturn("uri=/api/runtime");

        ResponseEntity<?> response = handler.handleRuntimeException(ex, request);

        assertThat(response.getStatusCode().value()).isEqualTo(400);
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertThat(body.get("message")).isEqualTo("runtime issue");
        assertThat(body.get("path")).isEqualTo("/api/runtime");
    }

    @Test
    void handleValidationException_Returns400WithFieldErrors() throws Exception {
        class DummyDto { public String name; }
        DummyDto target = new DummyDto();
        BeanPropertyBindingResult bindingResult =
                new BeanPropertyBindingResult(target, "dummy");
        bindingResult.addError(new FieldError("dummy", "name", "must not be blank"));

        MethodArgumentNotValidException ex =
                new MethodArgumentNotValidException(null, bindingResult);

        WebRequest request = mock(WebRequest.class);
        when(request.getDescription(false)).thenReturn("uri=/api/validate");

        ResponseEntity<?> response = handler.handleValidationException(ex, request);

        assertThat(response.getStatusCode().value()).isEqualTo(400);
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertThat(body.get("message")).isEqualTo("Validation failed");
        assertThat(body.get("path")).isEqualTo("/api/validate");

        Map<?, ?> errors = (Map<?, ?>) body.get("errors");
        assertThat(errors.get("name")).isEqualTo("must not be blank");
    }

    @Test
    void handleAccessDeniedException_Returns403() {
        AccessDeniedException ex = new AccessDeniedException("denied");
        WebRequest request = mock(WebRequest.class);
        when(request.getDescription(false)).thenReturn("uri=/api/secure");

        ResponseEntity<?> response = handler.handleAccessDeniedException(ex, request);

        assertThat(response.getStatusCode().value()).isEqualTo(403);
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertThat(body.get("message"))
                .isEqualTo("Access denied: You don't have permission to access this resource");
        assertThat(body.get("path")).isEqualTo("/api/secure");
    }

    @Test
    void handleBadCredentialsException_Returns401() {
        BadCredentialsException ex = new BadCredentialsException("bad");
        WebRequest request = mock(WebRequest.class);
        when(request.getDescription(false)).thenReturn("uri=/api/login");

        ResponseEntity<?> response = handler.handleBadCredentialsException(ex, request);

        assertThat(response.getStatusCode().value()).isEqualTo(401);
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertThat(body.get("message")).isEqualTo("Invalid username or password");
        assertThat(body.get("path")).isEqualTo("/api/login");
    }

    @Test
    void handleIllegalArgumentException_Returns400() {
        IllegalArgumentException ex = new IllegalArgumentException("bad arg");
        WebRequest request = mock(WebRequest.class);
        when(request.getDescription(false)).thenReturn("uri=/api/arg");

        ResponseEntity<?> response = handler.handleIllegalArgumentException(ex, request);

        assertThat(response.getStatusCode().value()).isEqualTo(400);
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertThat(body.get("message")).isEqualTo("bad arg");
        assertThat(body.get("path")).isEqualTo("/api/arg");
    }

    @Test
    void handleResourceNotFoundException_Returns404() {
        ResourceNotFoundException ex = new ResourceNotFoundException("not found");
        WebRequest request = mock(WebRequest.class);
        when(request.getDescription(false)).thenReturn("uri=/api/missing");

        ResponseEntity<?> response = handler.handleResourceNotFoundException(ex, request);

        assertThat(response.getStatusCode().value()).isEqualTo(404);
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertThat(body.get("message")).isEqualTo("not found");
        assertThat(body.get("path")).isEqualTo("/api/missing");
    }
}