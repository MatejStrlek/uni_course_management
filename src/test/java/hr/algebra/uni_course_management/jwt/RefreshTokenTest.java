package hr.algebra.uni_course_management.jwt;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class RefreshTokenTest {
    @Test
    void onCreate_SetsCreatedDate() {
        RefreshToken token = new RefreshToken();
        assertThat(token.getCreatedDate()).isNull();
        token.onCreate();
        assertThat(token.getCreatedDate()).isNotNull();
    }

    @Test
    void isExpired_ReturnsFalse_WhenExpiryInFuture() {
        RefreshToken token = new RefreshToken();
        token.setExpiryDate(LocalDateTime.now().plusMinutes(5));
        boolean expired = token.isExpired();
        assertThat(expired).isFalse();
    }

    @Test
    void isExpired_ReturnsTrue_WhenExpiryInPast() {
        RefreshToken token = new RefreshToken();
        token.setExpiryDate(LocalDateTime.now().minusMinutes(5));
        boolean expired = token.isExpired();
        assertThat(expired).isTrue();
    }
}