package hr.algebra.uni_course_management.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class EnrollmentStatusTest {
    @Test
    void valueOf_ParsesAllDefinedStatuses() {
        assertThat(EnrollmentStatus.valueOf("ENROLLED")).isEqualTo(EnrollmentStatus.ENROLLED);
        assertThat(EnrollmentStatus.valueOf("DROPPED")).isEqualTo(EnrollmentStatus.DROPPED);
        assertThat(EnrollmentStatus.valueOf("COMPLETED")).isEqualTo(EnrollmentStatus.COMPLETED);
    }
}
