package hr.algebra.uni_course_management.model;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class SemesterTest {
    @Test
    void getDisplayName_ReturnsCorrectStrings() {
        assertThat(Semester.WINTER.getDisplayName())
                .isEqualTo("Winter semester");
        assertThat(Semester.SUMMER.getDisplayName())
                .isEqualTo("Summer semester");
    }

    @Test
    void allSemesters_HaveNonEmptyDisplayName() {
        for (Semester semester : Semester.values()) {
            String display = semester.getDisplayName();
            assertThat(display).isNotNull().isNotBlank();
        }
    }
}