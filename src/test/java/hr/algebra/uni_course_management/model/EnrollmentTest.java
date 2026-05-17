package hr.algebra.uni_course_management.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class EnrollmentTest {
    @Test
    void defaultConstructor_SetsEnrolledAtByDefault() {
        Enrollment enrollment = new Enrollment();

        assertThat(enrollment.getEnrolledAt()).isNotNull();
    }

    @Test
    void settersAndGetters_WorkForCoreFields() {
        Enrollment enrollment = new Enrollment();
        User student = new User();
        Course course = new Course();
        Grade grade = new Grade();

        enrollment.setId(1L);
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setStatus(EnrollmentStatus.ENROLLED);
        enrollment.setTempGrade(grade);

        assertThat(enrollment.getId()).isEqualTo(1L);
        assertThat(enrollment.getStudent()).isSameAs(student);
        assertThat(enrollment.getCourse()).isSameAs(course);
        assertThat(enrollment.getStatus()).isEqualTo(EnrollmentStatus.ENROLLED);
        assertThat(enrollment.getTempGrade()).isSameAs(grade);
    }
}
