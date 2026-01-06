package hr.algebra.uni_course_management.controller;

import hr.algebra.uni_course_management.model.User;
import hr.algebra.uni_course_management.model.UserRole;
import hr.algebra.uni_course_management.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.security.Principal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    @Mock
    private UserService userService;
    @Mock
    private Model model;
    @InjectMocks
    private UserController userController;

    @Test
    void redirectToLogin_ReturnsRedirectView() {
        String view = userController.redirectToLogin();
        assertThat(view).isEqualTo("redirect:/login");
    }

    @Test
    void login_WithoutError_ReturnsLoginWithoutErrorMessage() {
        String view = userController.login(null, model);
        assertThat(view).isEqualTo("login");
        verify(model, never()).addAttribute(eq("errorMessage"), any());
    }

    @Test
    void login_WithError_AddsErrorMessage() {
        String view = userController.login("true", model);
        assertThat(view).isEqualTo("login");
        verify(model).addAttribute("errorMessage", "Invalid username or password!");
    }

    @Test
    void dashboard_WithPrincipal_AddsUserAndRole() {
        Principal principal = () -> "john";
        User user = new User();
        user.setUsername("john");
        user.setRole(UserRole.STUDENT);

        when(userService.getCurrentUser("john")).thenReturn(user);
        String view = userController.dashboard(principal, model);

        assertThat(view).isEqualTo("dashboard");
        verify(userService).getCurrentUser("john");
        verify(model).addAttribute("primaryRole", "STUDENT");
        verify(model).addAttribute("currentUser", user);
    }

    @Test
    void dashboard_WithoutPrincipal_DoesNotCallService() {
        String view = userController.dashboard(null, model);
        assertThat(view).isEqualTo("dashboard");
        verifyNoInteractions(userService);
        verifyNoInteractions(model);
    }
}