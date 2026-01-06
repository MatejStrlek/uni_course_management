package hr.algebra.uni_course_management.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {
    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter filter;

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_NoAuthHeader_JustContinuesChain() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void doFilterInternal_InvalidPrefix_JustContinuesChain() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Token abc");

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void doFilterInternal_ValidToken_SetsAuthenticationAndContinues() throws Exception {
        String jwt = "jwt-token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + jwt);
        when(jwtUtil.extractUsername(jwt)).thenReturn("john");
        when(jwtUtil.isTokenExpired(jwt)).thenReturn(false);

        UserDetails userDetails = new User("john", "password", emptyList());
        when(userDetailsService.loadUserByUsername("john")).thenReturn(userDetails);

        filter.doFilterInternal(request, response, filterChain);

        verify(jwtUtil).extractUsername(jwt);
        verify(userDetailsService).loadUserByUsername("john");
        verify(filterChain).doFilter(request, response);

        var auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isInstanceOf(UsernamePasswordAuthenticationToken.class);
        assertThat(auth.getName()).isEqualTo("john");
    }

    @Test
    void doFilterInternal_ExpiredToken_DoesNotAuthenticate() throws Exception {
        String jwt = "expired-jwt";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + jwt);
        when(jwtUtil.extractUsername(jwt)).thenReturn("john");
        when(jwtUtil.isTokenExpired(jwt)).thenReturn(true);

        filter.doFilterInternal(request, response, filterChain);

        verify(jwtUtil).extractUsername(jwt);
        verify(jwtUtil).isTokenExpired(jwt);
        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void doFilterInternal_ExceptionDuringProcessing_StillContinuesChain() throws Exception {
        String jwt = "bad-jwt";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + jwt);
        when(jwtUtil.extractUsername(jwt)).thenThrow(new RuntimeException("parse error"));

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void doFilterInternal_AuthenticationAlreadyPresent_DoesNotOverride() throws Exception {
        var existingAuth = new UsernamePasswordAuthenticationToken("existing", null, emptyList());
        SecurityContextHolder.getContext().setAuthentication(existingAuth);

        String jwt = "jwt-token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + jwt);
        when(jwtUtil.extractUsername(jwt)).thenReturn("john");

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isSameAs(existingAuth);
    }
}