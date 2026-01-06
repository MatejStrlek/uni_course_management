package hr.algebra.uni_course_management.jwt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.Date;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class JwtUtilTest {
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", "test-secret-key-12345678901234567890");
        ReflectionTestUtils.setField(jwtUtil, "accessTokenExpiration", 3600_000L);
    }

    @Test
    void generateAccessToken_AndExtractClaims_WorkCorrectly() {
        UserDetails userDetails =
                new User("john", "pass", Collections.emptyList());
        String token = jwtUtil.generateAccessToken(userDetails, "ROLE_STUDENT");

        String username = jwtUtil.extractUsername(token);
        Date expiration = jwtUtil.extractExpiration(token);
        String role = jwtUtil.extractRole(token);
        Boolean expired = jwtUtil.isTokenExpired(token);
        Boolean valid = jwtUtil.validateToken(token, userDetails);

        assertThat(username).isEqualTo("john");
        assertThat(role).isEqualTo("ROLE_STUDENT");
        assertThat(expiration).isAfter(new Date());
        assertThat(expired).isFalse();
        assertThat(valid).isTrue();
    }

    @Test
    void validateToken_ReturnsFalse_WhenUsernameDoesNotMatch() {
        UserDetails userDetails =
                new User("other", "pass", Collections.emptyList());
        String token = jwtUtil.generateAccessToken(
                new User("john", "pass", Collections.emptyList()),
                "ROLE_STUDENT"
        );

        Boolean valid = jwtUtil.validateToken(token, userDetails);
        assertThat(valid).isFalse();
    }
}