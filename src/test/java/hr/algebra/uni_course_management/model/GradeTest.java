package hr.algebra.uni_course_management.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class GradeTest {
    @Test
    void noArgsConstructor_SetsGradedAtByDefault() {
        Grade grade = new Grade();

        assertThat(grade.getGradedAt()).isNotNull();
    }

    @Test
    void allArgsConstructor_SetsAllFields() {
        Enrollment enrollment = new Enrollment();
        Grade grade = new Grade(5L, enrollment, 4, null);

        assertThat(grade.getId()).isEqualTo(5L);
        assertThat(grade.getEnrollment()).isSameAs(enrollment);
        assertThat(grade.getGradeValue()).isEqualTo(4);
        assertThat(grade.getGradedAt()).isNull();
    }
}
