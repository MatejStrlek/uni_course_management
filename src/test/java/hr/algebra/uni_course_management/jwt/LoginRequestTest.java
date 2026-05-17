package hr.algebra.uni_course_management.jwt;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LoginRequestTest {
    private final Validator validator;

    LoginRequestTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    @Test
    void allArgsConstructor_SetsFields() {
        LoginRequest request = new LoginRequest("john", "secret");

        assertThat(request.getUsername()).isEqualTo("john");
        assertThat(request.getPassword()).isEqualTo("secret");
    }

    @Test
    void validation_RejectsBlankFields() {
        LoginRequest request = new LoginRequest("", "");

        assertThat(validator.validate(request)).hasSize(2);
    }
}
