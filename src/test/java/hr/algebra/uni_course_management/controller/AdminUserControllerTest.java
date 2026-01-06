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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminUserControllerTest {
    @Mock
    private UserService userService;
    @Mock
    private Model model;
    @Mock
    private RedirectAttributes redirectAttributes;
    @InjectMocks
    private AdminUserController controller;

    @Test
    void viewUsers_noRole_returnsAllUsers() {
        List<User> allUsers = List.of(new User(), new User());
        when(userService.getAllUsers()).thenReturn(allUsers);

        String view = controller.viewUsers(null, model);

        assertThat(view).isEqualTo("admin/users/list");
        verify(userService).getAllUsers();
        verify(model).addAttribute("users", allUsers);
        verify(model).addAttribute("selectedRole", null);
        verify(model).addAttribute("roles", UserRole.values());
    }

    @Test
    void viewUsers_withRole_filtersByRole() {
        List<User> adminUsers = List.of(new User());
        when(userService.getUsersByRole(UserRole.ADMIN)).thenReturn(adminUsers);

        String view = controller.viewUsers("admin", model);

        assertThat(view).isEqualTo("admin/users/list");
        verify(userService).getUsersByRole(UserRole.ADMIN);
        verify(model).addAttribute("users", adminUsers);
        verify(model).addAttribute("selectedRole", "admin");
    }

    @Test
    void editUserForm_addsUserAndRolesToModel() {
        User user = new User();
        user.setId(1L);
        when(userService.getUserById(1L)).thenReturn(user);

        String view = controller.editUserForm(1L, model);

        assertThat(view).isEqualTo("admin/users/edit");
        verify(userService).getUserById(1L);
        verify(model).addAttribute("user", user);
        verify(model).addAttribute("roles", UserRole.values());
    }

    @Test
    void updateUser_success_redirectsWithSuccessMessage() {
        User user = new User();
        user.setUsername("testuser");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john@test.com");
        user.setRole(UserRole.STUDENT);
        user.setPassword("hashedpass");
        user.setIsActive(true);

        String view = controller.updateUser(1L, user, redirectAttributes, model);

        assertThat(view).isEqualTo("redirect:/admin/users");
        verify(userService).updateUser(
                eq(1L), eq("testuser"), eq("John"), eq("Doe"),
                eq("john@test.com"), eq(UserRole.STUDENT), eq("hashedpass"), eq(true)
        );
        verify(redirectAttributes).addFlashAttribute("successMessage", "User 'testuser' updated successfully!");
    }

    @Test
    void updateUser_exception_addsErrorAndRedirectsToEdit() {
        User user = new User();
        user.setUsername("testuser");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("invalid-email");
        user.setRole(UserRole.STUDENT);
        user.setPassword("hashedpass");
        user.setIsActive(true);

        doThrow(new IllegalArgumentException("Invalid email"))
                .when(userService).updateUser(anyLong(), anyString(), anyString(), anyString(), anyString(), any(), anyString(), anyBoolean());
        when(userService.getUserById(1L)).thenReturn(user);

        String view = controller.updateUser(1L, user, redirectAttributes, model);

        assertThat(view).isEqualTo("redirect:/admin/users/edit/1");
        verify(redirectAttributes, never()).addFlashAttribute(eq("successMessage"), any());
        verify(model).addAttribute("errorMessage", "Invalid email");
        verify(userService).getUserById(1L);
    }

    @Test
    void deleteUser_success_redirectsWithSuccessMessage() {
        User user = new User();
        user.setUsername("testuser");
        when(userService.getUserById(1L)).thenReturn(user);

        String view = controller.deleteUser(1L, redirectAttributes);

        assertThat(view).isEqualTo("redirect:/admin/users");
        verify(userService).getUserById(1L);
        verify(userService).deleteUser(1L);
        verify(redirectAttributes).addFlashAttribute("successMessage", "User 'testuser' deleted successfully!");
    }

    @Test
    void deleteUser_exception_redirectsWithErrorMessage() {
        User user = new User();
        user.setUsername("testuser");
        when(userService.getUserById(1L)).thenReturn(user);
        doThrow(new IllegalArgumentException("Cannot delete admin"))
                .when(userService).deleteUser(1L);

        String view = controller.deleteUser(1L, redirectAttributes);

        assertThat(view).isEqualTo("redirect:/admin/users");
        verify(userService).getUserById(1L);
        verify(userService).deleteUser(1L);
        verify(redirectAttributes).addFlashAttribute("errorMessage", "Cannot delete admin");
    }

    @Test
    void showRegistrationForm_returnsRegisterView() {
        String view = controller.showRegistrationForm();

        assertThat(view).isEqualTo("register");
    }

    @Test
    void registerUser_success_redirectsToDashboard() {
        RedirectAttributes ra = mock(RedirectAttributes.class);
        Model m = mock(Model.class);

        String view = controller.registerUser("newuser", "pass", "John", "Doe", "john@test.com", "STUDENT", ra, m);

        assertThat(view).isEqualTo("redirect:/dashboard");
        verify(userService).registerUser("newuser", "pass", "John", "Doe", "john@test.com", UserRole.STUDENT);
        verify(ra).addFlashAttribute("successMessage", "User 'newuser' created successfully!");
        verify(m, never()).addAttribute(eq("errorMessage"), any());
    }

    @Test
    void registerUser_exception_addsErrorAndReturnsRegister() {
        Model m = mock(Model.class); // fresh mock for isolation
        RedirectAttributes ra = mock(RedirectAttributes.class); // fresh mock for isolation

        doThrow(new IllegalArgumentException("Username exists"))
                .when(userService).registerUser(anyString(), anyString(), anyString(), anyString(), anyString(), any());

        String view = controller.registerUser("newuser", "pass", "John", "Doe", "john@test.com", "STUDENT", ra, m);

        assertThat(view).isEqualTo("redirect:/admin/users/register");
        verify(m).addAttribute("errorMessage", "Username exists");
    }
}