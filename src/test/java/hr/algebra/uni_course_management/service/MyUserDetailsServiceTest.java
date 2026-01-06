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
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MyUserDetailsServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MyUserDetailsService myUserDetailsService;

    private User activeUser;
    private User disabledUser;

    @BeforeEach
    void setUp() {
        activeUser = new User();
        activeUser.setUsername("john");
        activeUser.setPassword("encodedPass");
        activeUser.setRole(UserRole.STUDENT);
        activeUser.setIsActive(true);

        disabledUser = new User();
        disabledUser.setUsername("jane");
        disabledUser.setPassword("encodedPass2");
        disabledUser.setRole(UserRole.PROFESSOR);
        disabledUser.setIsActive(false);
    }

    @Test
    void loadUserByUsername_ExistingActiveUser_ReturnsUserDetails() {
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(activeUser));

        UserDetails details = myUserDetailsService.loadUserByUsername("john");

        assertThat(details.getUsername()).isEqualTo("john");
        assertThat(details.getPassword()).isEqualTo("encodedPass");
        assertThat(details.getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_STUDENT");
        assertThat(details.isEnabled()).isTrue();
    }

    @Test
    void loadUserByUsername_UserNotFound_ThrowsUsernameNotFoundException() {
        when(userRepository.findByUsername("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> myUserDetailsService.loadUserByUsername("missing"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User not found: missing");
    }

    @Test
    void loadUserByUsername_DisabledUser_ThrowsDisabledException() {
        when(userRepository.findByUsername("jane")).thenReturn(Optional.of(disabledUser));

        assertThatThrownBy(() -> myUserDetailsService.loadUserByUsername("jane"))
                .isInstanceOf(DisabledException.class)
                .hasMessage("User account is disabled");
    }
}