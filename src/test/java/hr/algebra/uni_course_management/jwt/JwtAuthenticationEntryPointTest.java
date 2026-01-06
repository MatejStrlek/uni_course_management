package hr.algebra.uni_course_management.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.AuthenticationException;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

class JwtAuthenticationEntryPointTest {
    private final JwtAuthenticationEntryPoint entryPoint = new JwtAuthenticationEntryPoint();

    @Test
    void commence_WritesUnauthorizedJsonResponse() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        AuthenticationException ex = mock(AuthenticationException.class);

        when(request.getRequestURI()).thenReturn("/api/test");
        when(ex.getMessage()).thenReturn("Bad credentials");

        StringWriter bodyWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(bodyWriter);
        when(response.getWriter()).thenReturn(printWriter);

        entryPoint.commence(request, response, ex);

        verify(response).setContentType("application/json");
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        printWriter.flush();
        String json = bodyWriter.toString();

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> body = mapper.readValue(json, Map.class);

        assertThat(body.get("error")).isEqualTo("Unauthorized");
        assertThat(body.get("message")).isEqualTo("Bad credentials");
        assertThat(body.get("path")).isEqualTo("/api/test");
    }
}