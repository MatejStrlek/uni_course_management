package hr.algebra.uni_course_management.jwt;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class RefreshTokenRequestTest {
    private final Validator validator;

    RefreshTokenRequestTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    @Test
    void allArgsConstructor_SetsField() {
        RefreshTokenRequest request = new RefreshTokenRequest("token-123");

        assertThat(request.getRefreshToken()).isEqualTo("token-123");
    }

    @Test
    void validation_RejectsBlankRefreshToken() {
        RefreshTokenRequest request = new RefreshTokenRequest(" ");

        assertThat(validator.validate(request)).hasSize(1);
    }
}
