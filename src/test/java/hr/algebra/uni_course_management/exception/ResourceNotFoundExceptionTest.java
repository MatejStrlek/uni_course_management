package hr.algebra.uni_course_management.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class ResourceNotFoundExceptionTest {
    @Test
    void constructor_SetsExceptionMessage() {
        ResourceNotFoundException exception = new ResourceNotFoundException("Course not found");

        assertThat(exception).hasMessage("Course not found");
    }
}
