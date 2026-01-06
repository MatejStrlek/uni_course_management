package hr.algebra.uni_course_management.controller.api;

import hr.algebra.uni_course_management.jwt.*;
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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.core.userdetails.User.withUsername;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock private UserDetailsService userDetailsService;
    @Mock private JwtUtil jwtUtil;
    @Mock private UserService userService;
    @Mock private RefreshTokenService refreshTokenService;

    @InjectMocks
    private AuthController controller;

    private LoginRequest loginRequest;
    private User user;
    private UserDetails userDetails;
    private String accessToken;
    private RefreshToken refreshToken;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest("admin", "password");
        user = new User();
        user.setId(1L);
        user.setUsername("admin");
        user.setRole(UserRole.ADMIN);

        userDetails = withUsername("admin")
                .password("password")
                .roles("ADMIN")
                .build();
        accessToken = "jwt.access.token";
        refreshToken = new RefreshToken();
        refreshToken.setToken("refresh.token");
        refreshToken.setUser(user);
        ReflectionTestUtils.setField(controller, "accessTokenExpiration", 86400000L);
    }

    @Test
    void login_success_returnsOkWithTokens() {
        // Given
        doAnswer(invocation -> null).when(authenticationManager).authenticate(any());
        when(userDetailsService.loadUserByUsername("admin")).thenReturn(userDetails);
        when(userService.findByUsername("admin")).thenReturn(Optional.of(user));
        when(jwtUtil.generateAccessToken(userDetails, "ADMIN")).thenReturn(accessToken);
        when(refreshTokenService.createRefreshToken(user)).thenReturn(refreshToken);

        // When
        var response = controller.login(loginRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).extracting("success").isEqualTo(true);
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(refreshTokenService).createRefreshToken(user);
    }

    @Test
    void login_badCredentials_returnsUnauthorized() {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // When
        var response = controller.login(loginRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).extracting("success").isEqualTo(false);
        assertThat(response.getBody()).extracting("message").isEqualTo("Invalid username or password");
    }

    @Test
    void login_userNotFound_returnsInternalServerError() {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mock(Authentication.class));

        when(userDetailsService.loadUserByUsername("admin")).thenReturn(userDetails);
        when(userService.findByUsername("admin")).thenReturn(Optional.empty());

        // When
        var response = controller.login(loginRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        verify(userService).findByUsername("admin");
    }

    @Test
    void getCurrentUser_validToken_returnsUserData() {
        String authHeader = "Bearer valid.token";
        when(jwtUtil.extractUsername("valid.token")).thenReturn("admin");
        when(userService.findByUsername("admin")).thenReturn(Optional.of(user));

        var response = controller.getCurrentUser(authHeader);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertThat(body.get("username")).isEqualTo("admin");
        assertThat(body.get("role")).isEqualTo("ADMIN");
        verify(jwtUtil).extractUsername("valid.token");
    }

    @Test
    void getCurrentUser_invalidToken_returnsUnauthorized() {
        String authHeader = "Bearer invalid.token";
        var response = controller.getCurrentUser(authHeader);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

        @SuppressWarnings("unchecked")
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertThat(body.get("error")).isNotNull();
    }

    @Test
    void refreshToken_valid_returnsNewAccessToken() {
        // Given
        RefreshTokenRequest request = new RefreshTokenRequest("valid.refresh");
        when(refreshTokenService.findByToken("valid.refresh")).thenReturn(Optional.of(refreshToken));
        when(refreshTokenService.verifyExpiration(refreshToken)).thenReturn(refreshToken);
        when(userDetailsService.loadUserByUsername("admin")).thenReturn(userDetails);

        when(jwtUtil.generateAccessToken(userDetails, "ADMIN")).thenReturn("new.access.token");

        // When
        var response = controller.refreshToken(request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).extracting("success").isEqualTo(true);
        verify(refreshTokenService).verifyExpiration(refreshToken);
    }

    @Test
    void refreshToken_notFound_returnsUnauthorized() {
        // Given
        RefreshTokenRequest request = new RefreshTokenRequest("invalid.refresh");
        when(refreshTokenService.findByToken("invalid.refresh")).thenReturn(Optional.empty());

        // When
        var response = controller.refreshToken(request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).extracting("success").isEqualTo(false);
    }

    @Test
    void logout_success_returnsOk() {
        // Given
        String authHeader = "Bearer logout.token";
        when(jwtUtil.extractUsername("logout.token")).thenReturn("admin");
        when(userService.findByUsername("admin")).thenReturn(Optional.of(user));

        // When
        var response = controller.logout(authHeader);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).extracting("success").isEqualTo(true);
        verify(refreshTokenService).deleteByUser(user);
    }

    @Test
    void testRoles_returnsPrincipalAndAuthorities() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testuser");
        when(authentication.getAuthorities()).thenReturn(java.util.List.of());

        var response = controller.testRoles(authentication);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertThat(body.get("principal")).isEqualTo("testuser");
    }
}