package hr.algebra.uni_course_management.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class GradeReportRowTest {
    @Test
    void settersAndGetters_WorkForAllFields() {
        GradeReportRow row = new GradeReportRow();
        row.setStudentFirstName("Ana");
        row.setStudentLastName("Horvat");
        row.setCourseName("Databases");
        row.setGradeValue(5);
        row.setGradedAt("2026-05-17");

        assertThat(row.getStudentFirstName()).isEqualTo("Ana");
        assertThat(row.getStudentLastName()).isEqualTo("Horvat");
        assertThat(row.getCourseName()).isEqualTo("Databases");
        assertThat(row.getGradeValue()).isEqualTo(5);
        assertThat(row.getGradedAt()).isEqualTo("2026-05-17");
    }
}
