package hr.algebra.uni_course_management.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.ui.Model;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {
    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleNotFoundException_Returns404ViewAndMessage() {
        NoHandlerFoundException ex =
                new NoHandlerFoundException("GET", "/missing", null);
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost/missing"));
        Model model = mock(Model.class);

        String view = handler.handleNotFoundException(ex, request, model);

        assertThat(view).isEqualTo("error/404");
        verify(model).addAttribute("errorMessage", "The requested page was not found");
    }

    @Test
    void handleNoResourceFoundException_Returns404ViewAndMessage() {
        NoResourceFoundException ex =
                new NoResourceFoundException(HttpMethod.GET, "/resource");
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost/resource"));
        Model model = mock(Model.class);

        String view = handler.handleNoResourceFoundException(ex, request, model);

        assertThat(view).isEqualTo("error/404");
        verify(model).addAttribute("errorMessage", "The requested resource was not found");
    }

    @Test
    void handleGeneralException_Returns500ViewAndMessage() {
        Exception ex = new RuntimeException("boom");
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost/error"));
        Model model = mock(Model.class);

        String view = handler.handleGeneralException(ex, request, model);

        assertThat(view).isEqualTo("error/500");
        verify(model).addAttribute("errorMessage", "An unexpected error occurred");
    }

    @Test
    void handleIllegalArgumentException_Returns400ViewAndUsesExceptionMessage() {
        IllegalArgumentException ex = new IllegalArgumentException("Bad input");
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost/bad"));
        Model model = mock(Model.class);

        String view = handler.handleIllegalArgumentException(ex, request, model);

        assertThat(view).isEqualTo("error/400");
        verify(model).addAttribute("errorMessage", "Bad input");
    }
}