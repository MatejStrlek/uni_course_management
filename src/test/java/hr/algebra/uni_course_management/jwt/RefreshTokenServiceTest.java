package hr.algebra.uni_course_management.jwt;

import hr.algebra.uni_course_management.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {
    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @InjectMocks
    private RefreshTokenService refreshTokenService;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        ReflectionTestUtils.setField(refreshTokenService,
                "refreshTokenExpiration", 3_600_000L);
    }

    @Test
    void createRefreshToken_DeletesOldAndCreatesNew() {
        when(refreshTokenRepository.save(any(RefreshToken.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        RefreshToken token = refreshTokenService.createRefreshToken(user);

        verify(refreshTokenRepository).deleteByUser(user);
        verify(refreshTokenRepository).save(any(RefreshToken.class));
        assertThat(token.getUser()).isEqualTo(user);
        assertThat(token.getToken()).isNotBlank();
        assertThat(token.getExpiryDate()).isAfter(LocalDateTime.now());
        assertThat(token.getRevoked()).isFalse();
    }

    @Test
    void findByToken_DelegatesToRepository() {
        RefreshToken token = new RefreshToken();
        when(refreshTokenRepository.findByToken("abc"))
                .thenReturn(Optional.of(token));

        Optional<RefreshToken> result = refreshTokenService.findByToken("abc");

        assertThat(result).contains(token);
        verify(refreshTokenRepository).findByToken("abc");
    }

    @Test
    void verifyExpiration_NotExpired_ReturnsToken() {
        RefreshToken token = new RefreshToken();
        token.setExpiryDate(LocalDateTime.now().plusMinutes(5));

        RefreshToken result = refreshTokenService.verifyExpiration(token);

        assertThat(result).isSameAs(token);
        verify(refreshTokenRepository, never()).delete(any());
    }

    @Test
    void verifyExpiration_Expired_DeletesAndThrows() {
        RefreshToken token = new RefreshToken();
        token.setExpiryDate(LocalDateTime.now().minusMinutes(1));

        assertThatThrownBy(() -> refreshTokenService.verifyExpiration(token))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Refresh token expired. Please login again.");

        verify(refreshTokenRepository).delete(token);
    }

    @Test
    void deleteByUser_DelegatesToRepository() {
        refreshTokenService.deleteByUser(user);
        verify(refreshTokenRepository).deleteByUser(user);
    }

    @Test
    void deleteExpiredTokens_DelegatesWithCurrentTime() {
        refreshTokenService.deleteExpiredTokens();
        verify(refreshTokenRepository).deleteExpiredTokens(any(LocalDateTime.class));
    }
}