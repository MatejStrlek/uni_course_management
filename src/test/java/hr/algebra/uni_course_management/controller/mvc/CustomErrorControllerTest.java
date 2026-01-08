package hr.algebra.uni_course_management.controller.mvc;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomErrorControllerTest {
    private final CustomErrorController controller = new CustomErrorController();
    @Mock
    private HttpServletRequest request;

    @Test
    void handleError_403_returns403View() {
        when(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE))
                .thenReturn(HttpStatus.FORBIDDEN.value());
        String view = controller.handleError(request);
        assertThat(view).isEqualTo("error/403");
    }

    @Test
    void handleError_404_returns404View() {
        when(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE))
                .thenReturn(HttpStatus.NOT_FOUND.value());
        String view = controller.handleError(request);
        assertThat(view).isEqualTo("error/404");
    }

    @Test
    void handleError_500_returns500View() {
        when(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE))
                .thenReturn(HttpStatus.INTERNAL_SERVER_ERROR.value());
        String view = controller.handleError(request);
        assertThat(view).isEqualTo("error/500");
    }

    @Test
    void handleError_400_returns400View() {
        when(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE))
                .thenReturn(HttpStatus.BAD_REQUEST.value());
        String view = controller.handleError(request);
        assertThat(view).isEqualTo("error/400");
    }

    @Test
    void handleError_nullOrUnknown_returns500View() {
        when(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE))
                .thenReturn(null);
        String view = controller.handleError(request);
        assertThat(view).isEqualTo("error/500");
    }

    @Test
    void errorEndpoints_returnCorrectViews() {
        assertThat(controller.error403()).isEqualTo("error/403");
        assertThat(controller.error404()).isEqualTo("error/404");
        assertThat(controller.error500()).isEqualTo("error/500");
        assertThat(controller.error400()).isEqualTo("error/400");
    }
}