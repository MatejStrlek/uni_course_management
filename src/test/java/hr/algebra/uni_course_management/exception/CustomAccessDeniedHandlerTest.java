package hr.algebra.uni_course_management.exception;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;

import java.io.IOException;

import static org.mockito.Mockito.*;

class CustomAccessDeniedHandlerTest {
    private final CustomAccessDeniedHandler handler = new CustomAccessDeniedHandler();

    @Test
    void handle_ForwardsTo403ErrorPage() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        AccessDeniedException ex = new AccessDeniedException("denied");

        when(request.getRequestURI()).thenReturn("/admin/page");
        RequestDispatcher dispatcher = mock(RequestDispatcher.class);
        when(request.getRequestDispatcher("/error/403")).thenReturn(dispatcher);

        handler.handle(request, response, ex);

        verify(request).getRequestDispatcher("/error/403");
        verify(dispatcher).forward(request, response);
    }
}