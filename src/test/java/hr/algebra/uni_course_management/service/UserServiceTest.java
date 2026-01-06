package hr.algebra.uni_course_management.service;
import hr.algebra.uni_course_management.model.User;
import hr.algebra.uni_course_management.model.UserRole;
import hr.algebra.uni_course_management.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setEmail("test@example.com");
        testUser.setRole(UserRole.STUDENT);
        testUser.setIsActive(true);
        testUser.setPassword("oldEncoded");
    }

    // -------- registerUser --------

    @Test
    void registerUser_ValidData_SavesUser() {
        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User result = userService.registerUser(
                "newuser", "password123", "New", "User", "new@example.com", UserRole.STUDENT);

        assertThat(result).isNotNull();
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerUser_UsernameExists_ThrowsException() {
        when(userRepository.findByUsername("existing")).thenReturn(Optional.of(testUser));

        assertThatThrownBy(() -> userService.registerUser(
                "existing", "pass123", "F", "L", "e@example.com", UserRole.STUDENT))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Username already exists: existing");
    }

    @Test
    void registerUser_EmailExists_ThrowsException() {
        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(userRepository.existsByEmail("duplicate@example.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.registerUser(
                "newuser", "pass123", "F", "L", "duplicate@example.com", UserRole.STUDENT))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email already exists: duplicate@example.com");
    }

    @Test
    void registerUser_ShortPassword_ThrowsException() {
        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);

        assertThatThrownBy(() -> userService.registerUser(
                "newuser", "123", "F", "L", "new@example.com", UserRole.STUDENT))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Password must be at least 6 characters long");
    }

    // -------- getCurrentUser --------

    @Test
    void getCurrentUser_Existing_ReturnsUser() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("currentUser");
        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);   // static holder, no mock needed [web:120][web:127]

        User current = new User();
        current.setUsername("currentUser");
        when(userRepository.findByUsername("currentUser")).thenReturn(Optional.of(current));

        User result = userService.getCurrentUser("ignored");

        assertThat(result.getUsername()).isEqualTo("currentUser");
        verify(userRepository).findByUsername("currentUser");
    }

    @Test
    void getCurrentUser_NotFound_ThrowsUsernameNotFound() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("missing");
        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);

        when(userRepository.findByUsername("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getCurrentUser("ignored"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User not found: ignored");
    }

    // -------- getAllUsers / getUsersByRole --------

    @Test
    void getAllUsers_ReturnsAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(testUser));

        List<User> result = userService.getAllUsers();

        assertThat(result).hasSize(1).contains(testUser);
        verify(userRepository).findAll();
    }

    @Test
    void getUsersByRole_ReturnsFilteredUsers() {
        when(userRepository.findByRole(UserRole.STUDENT)).thenReturn(List.of(testUser));

        List<User> result = userService.getUsersByRole(UserRole.STUDENT);

        assertThat(result).hasSize(1).contains(testUser);
        verify(userRepository).findByRole(UserRole.STUDENT);
    }

    // -------- getUserById / findByUsername --------

    @Test
    void getUserById_ExistingId_ReturnsUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        User result = userService.getUserById(1L);

        assertThat(result).isEqualTo(testUser);
        verify(userRepository).findById(1L);
    }

    @Test
    void getUserById_NonExisting_ThrowsException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User not found with id: 999");
    }

    @Test
    void findByUsername_Existing_ReturnsOptionalUser() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        Optional<User> result = userService.findByUsername("testuser");

        assertThat(result).contains(testUser);
        verify(userRepository).findByUsername("testuser");
    }

    @Test
    void findByUsername_NonExisting_ReturnsEmpty() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        Optional<User> result = userService.findByUsername("nonexistent");

        assertThat(result).isEmpty();
        verify(userRepository).findByUsername("nonexistent");
    }

    // -------- updateUser --------

    @Test
    void updateUser_SameUsername_NoPasswordChange_UpdatesFieldsOnly() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        userService.updateUser(1L, "testuser", "Updated", "User",
                "updated@example.com", UserRole.PROFESSOR, null, false);

        verify(userRepository).save(any(User.class));
        assertThat(testUser.getFirstName()).isEqualTo("Updated");
        assertThat(testUser.getLastName()).isEqualTo("User");
        assertThat(testUser.getEmail()).isEqualTo("updated@example.com");
        assertThat(testUser.getRole()).isEqualTo(UserRole.PROFESSOR);
        assertThat(testUser.getIsActive()).isFalse();
        assertThat(testUser.getPassword()).isEqualTo("oldEncoded"); // password unchanged
    }

    @Test
    void updateUser_NewUsernameAlreadyExists_ThrowsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        User other = new User();
        other.setUsername("newusername");
        when(userRepository.findByUsername("newusername")).thenReturn(Optional.of(other));

        assertThatThrownBy(() -> userService.updateUser(
                1L, "newusername", "F", "L", "e@example.com", UserRole.STUDENT, null, true))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Username already exists: newusername");
    }

    @Test
    void updateUser_NewUsernameAndPassword_UpdatesBoth() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("newpass123")).thenReturn("newEncodedPass");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        userService.updateUser(1L, "newuser", "NewF", "NewL",
                "new@example.com", UserRole.STUDENT, "newpass123", true);

        assertThat(testUser.getUsername()).isEqualTo("newuser");
        assertThat(testUser.getPassword()).isEqualTo("newEncodedPass");
        verify(passwordEncoder).encode("newpass123");
        verify(userRepository).save(any(User.class));
    }

    // -------- deleteUser --------

    @Test
    void deleteUser_ValidId_CallsRepositoryDelete() {
        doNothing().when(userRepository).deleteById(1L);

        userService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
    }
}