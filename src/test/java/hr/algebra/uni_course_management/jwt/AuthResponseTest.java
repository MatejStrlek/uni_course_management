package hr.algebra.uni_course_management.jwt;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class AuthResponseTest {
    @Test
    void customConstructor_SetsFieldsAndDefaultTokenType() {
        AuthResponse response = new AuthResponse(
                "access123",
                "refresh456",
                "john",
                "ROLE_STUDENT",
                3600L
        );

        assertThat(response.getAccessToken()).isEqualTo("access123");
        assertThat(response.getRefreshToken()).isEqualTo("refresh456");
        assertThat(response.getUsername()).isEqualTo("john");
        assertThat(response.getRole()).isEqualTo("ROLE_STUDENT");
        assertThat(response.getExpiresIn()).isEqualTo(3600L);
        assertThat(response.getTokenType()).isEqualTo("Bearer");
    }

    @Test
    void noArgsConstructor_AllowsManualSettingFields() {
        AuthResponse response = new AuthResponse();
        response.setAccessToken("a");
        response.setRefreshToken("r");
        response.setTokenType("Custom");
        response.setUsername("u");
        response.setRole("ROLE_ADMIN");
        response.setExpiresIn(120L);

        assertThat(response.getAccessToken()).isEqualTo("a");
        assertThat(response.getRefreshToken()).isEqualTo("r");
        assertThat(response.getTokenType()).isEqualTo("Custom");
        assertThat(response.getUsername()).isEqualTo("u");
        assertThat(response.getRole()).isEqualTo("ROLE_ADMIN");
        assertThat(response.getExpiresIn()).isEqualTo(120L);
    }
}