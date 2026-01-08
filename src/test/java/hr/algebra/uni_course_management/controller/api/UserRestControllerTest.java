package hr.algebra.uni_course_management.controller.api;

import hr.algebra.uni_course_management.exception.ResourceNotFoundException;
import hr.algebra.uni_course_management.model.User;
import hr.algebra.uni_course_management.model.UserRole;
import hr.algebra.uni_course_management.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRestControllerTest {
    @Mock
    private UserService userService;
    @InjectMocks
    private UserRestController controller;
    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = new User();
        sampleUser.setId(1L);
        sampleUser.setUsername("testuser");
        sampleUser.setFirstName("Test");
        sampleUser.setLastName("User");
        sampleUser.setEmail("test@example.com");
        sampleUser.setRole(UserRole.STUDENT);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllUsers_noRole_returnsAllUsers() {
        // Given
        when(userService.getAllUsers()).thenReturn(List.of(sampleUser));

        // When
        ResponseEntity<?> response = controller.getAllUsers(null);

        // Then
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(body.get("success")).isEqualTo(true);
        assertThat(((List<?>) body.get("data"))).hasSize(1);
        verify(userService).getAllUsers();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllUsers_withValidRole_returnsFilteredUsers() {
        // Given
        when(userService.getUsersByRole(UserRole.ADMIN)).thenReturn(List.of(sampleUser));

        // When
        ResponseEntity<?> response = controller.getAllUsers("admin");

        // Then
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(body.get("success")).isEqualTo(true);
        assertThat(((List<?>) body.get("data"))).hasSize(1);
        verify(userService).getUsersByRole(UserRole.ADMIN);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUserById_success_returnsOk() {
        // Given
        when(userService.getUserById(1L)).thenReturn(sampleUser);

        // When
        ResponseEntity<?> response = controller.getUserById(1L);

        // Then
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(body.get("success")).isEqualTo(true);
        assertThat(((User) body.get("data")).getUsername()).isEqualTo("testuser");
        verify(userService).getUserById(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUserById_notFound_returnsNotFound() {
        // Given
        when(userService.getUserById(999L)).thenThrow(new ResourceNotFoundException("User not found"));

        // When
        ResponseEntity<?> response = controller.getUserById(999L);

        // Then
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(body.get("success")).isEqualTo(false);
        assertThat(body.get("message")).isEqualTo("User not found");
        verify(userService).getUserById(999L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createUser_success_returnsCreated() {
        // Given
        when(userService.registerUser(anyString(), anyString(), anyString(), anyString(), anyString(), any(UserRole.class)))
                .thenReturn(sampleUser);

        // When
        ResponseEntity<?> response = controller.createUser("newuser", "password123", "New", "User", "new@example.com", "STUDENT");

        // Then
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(body.get("success")).isEqualTo(true);
        assertThat((String) body.get("message")).contains("newuser");
        verify(userService).registerUser("newuser", "password123", "New", "User", "new@example.com", UserRole.STUDENT);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createUser_illegalArgument_returnsBadRequest() {
        // Given
        when(userService.registerUser(anyString(), anyString(), anyString(), anyString(), anyString(), any(UserRole.class)))
                .thenThrow(new IllegalArgumentException("Username already exists"));

        // When
        ResponseEntity<?> response = controller.createUser("duplicate", "pass", "Dup", "User", "dup@example.com", "STUDENT");

        // Then
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(body.get("success")).isEqualTo(false);
        assertThat(body.get("message")).isEqualTo("Username already exists");
        verify(userService).registerUser("duplicate", "pass", "Dup", "User", "dup@example.com", UserRole.STUDENT);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteUser_success_returnsOk() {
        // Given
        when(userService.getUserById(1L)).thenReturn(sampleUser);
        doNothing().when(userService).deleteUser(1L);

        // When
        ResponseEntity<?> response = controller.deleteUser(1L);

        // Then
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(body.get("success")).isEqualTo(true);
        assertThat((String) body.get("message")).contains("testuser");
        verify(userService).getUserById(1L);
        verify(userService).deleteUser(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteUser_notFound_returnsNotFound() {
        // Given
        when(userService.getUserById(999L)).thenThrow(new ResourceNotFoundException("User not found"));

        // When
        ResponseEntity<?> response = controller.deleteUser(999L);

        // Then
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(body.get("success")).isEqualTo(false);
        assertThat(body.get("message")).isEqualTo("User not found");
        verify(userService).getUserById(999L);
        verify(userService, never()).deleteUser(anyLong());
    }
}