package hr.algebra.uni_course_management.jwt;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class ApiResponseTest {
    @Test
    void allArgsConstructor_SetsFieldsCorrectly() {
        ApiResponse<String> response = new ApiResponse<>(true, "ok", "data");

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getMessage()).isEqualTo("ok");
        assertThat(response.getData()).isEqualTo("data");
    }

    @Test
    void settersAndGetters_WorkForGenericType() {
        ApiResponse<Integer> response = new ApiResponse<>();
        response.setSuccess(false);
        response.setMessage("error");
        response.setData(42);

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getMessage()).isEqualTo("error");
        assertThat(response.getData()).isEqualTo(42);
    }
}