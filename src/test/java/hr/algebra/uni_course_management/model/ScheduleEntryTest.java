package hr.algebra.uni_course_management.model;

import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class ScheduleEntryTest {
    @Test
    void allArgsConstructor_SetsAllFields() {
        Course course = new Course();
        ScheduleEntry entry = new ScheduleEntry(
                10L,
                course,
                DayOfWeek.MONDAY,
                LocalTime.of(8, 0),
                LocalTime.of(10, 0),
                "A-101"
        );

        assertThat(entry.getId()).isEqualTo(10L);
        assertThat(entry.getCourse()).isSameAs(course);
        assertThat(entry.getDayOfWeek()).isEqualTo(DayOfWeek.MONDAY);
        assertThat(entry.getStartTime()).isEqualTo(LocalTime.of(8, 0));
        assertThat(entry.getEndTime()).isEqualTo(LocalTime.of(10, 0));
        assertThat(entry.getRoom()).isEqualTo("A-101");
    }
}

