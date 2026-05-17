package hr.algebra.uni_course_management.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class ScheduleRowTest {
    @Test
    void allArgsConstructor_SetsAllFields() {
        ScheduleRow row = new ScheduleRow(
                "MONDAY",
                LocalTime.of(9, 0),
                LocalTime.of(11, 0),
                "Algorithms",
                "CS201",
                "B-12"
        );

        assertThat(row.getDayOfWeek()).isEqualTo("MONDAY");
        assertThat(row.getStartTime()).isEqualTo(LocalTime.of(9, 0));
        assertThat(row.getEndTime()).isEqualTo(LocalTime.of(11, 0));
        assertThat(row.getCourseName()).isEqualTo("Algorithms");
        assertThat(row.getCourseCode()).isEqualTo("CS201");
        assertThat(row.getRoom()).isEqualTo("B-12");
    }
}
