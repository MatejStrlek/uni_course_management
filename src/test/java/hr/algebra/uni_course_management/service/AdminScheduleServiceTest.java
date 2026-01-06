package hr.algebra.uni_course_management.service;

import hr.algebra.uni_course_management.model.Course;
import hr.algebra.uni_course_management.model.ScheduleEntry;
import hr.algebra.uni_course_management.repository.CourseRepository;
import hr.algebra.uni_course_management.repository.ScheduleEntryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminScheduleServiceTest {
    @Mock
    private ScheduleEntryRepository scheduleEntryRepository;

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private AdminScheduleService adminScheduleService;

    private Course course;
    private ScheduleEntry entry;

    @BeforeEach
    void setUp() {
        course = new Course();
        course.setId(1L);

        entry = new ScheduleEntry();
        entry.setId(10L);
        entry.setCourse(course);
        entry.setDayOfWeek(DayOfWeek.MONDAY);
        entry.setStartTime(LocalTime.of(10, 0));
        entry.setEndTime(LocalTime.of(12, 0));
        entry.setRoom("A1");
    }

    // ---------- findAllScheduleEntriesSorted ----------

    @Test
    void findAllScheduleEntriesSorted_ReturnsEntries() {
        when(scheduleEntryRepository.findAllByOrderByDayOfWeekAscStartTimeAsc())
                .thenReturn(List.of(entry));

        List<ScheduleEntry> result = adminScheduleService.findAllScheduleEntriesSorted();

        assertThat(result).containsExactly(entry);
        verify(scheduleEntryRepository).findAllByOrderByDayOfWeekAscStartTimeAsc();
    }

    // ---------- getScheduleEntryById ----------

    @Test
    void getScheduleEntryById_Existing_ReturnsEntry() {
        when(scheduleEntryRepository.findById(10L)).thenReturn(Optional.of(entry));

        ScheduleEntry result = adminScheduleService.getScheduleEntryById(10L);

        assertThat(result).isEqualTo(entry);
        verify(scheduleEntryRepository).findById(10L);
    }

    @Test
    void getScheduleEntryById_NotExisting_Throws() {
        when(scheduleEntryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminScheduleService.getScheduleEntryById(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Schedule entry with ID 99 not found.");
    }

    // ---------- createScheduleEntry ----------

    @Test
    void createScheduleEntry_ValidCourse_SetsCourseAndSaves() {
        ScheduleEntry toCreate = new ScheduleEntry();
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(scheduleEntryRepository.save(any(ScheduleEntry.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        ScheduleEntry result = adminScheduleService.createScheduleEntry(1L, toCreate);

        assertThat(result.getCourse()).isEqualTo(course);
        verify(scheduleEntryRepository).save(result);
    }

    @Test
    void createScheduleEntry_CourseNotFound_Throws() {
        when(courseRepository.findById(1L)).thenReturn(Optional.empty());

        ScheduleEntry toCreate = new ScheduleEntry();

        assertThatThrownBy(() -> adminScheduleService.createScheduleEntry(1L, toCreate))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Course with ID 1 not found.");
    }

    // ---------- updateScheduleEntry ----------

    @Test
    void updateScheduleEntry_ValidIds_UpdatesFieldsAndSaves() {
        ScheduleEntry updated = new ScheduleEntry();
        updated.setDayOfWeek(DayOfWeek.FRIDAY);
        updated.setStartTime(LocalTime.of(8, 0));
        updated.setEndTime(LocalTime.of(9, 0));
        updated.setRoom("B2");

        when(scheduleEntryRepository.findById(10L)).thenReturn(Optional.of(entry));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(scheduleEntryRepository.save(any(ScheduleEntry.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        ScheduleEntry result = adminScheduleService.updateScheduleEntry(10L, 1L, updated);

        assertThat(result.getCourse()).isEqualTo(course);
        assertThat(result.getDayOfWeek()).isEqualTo(DayOfWeek.FRIDAY);
        assertThat(result.getStartTime()).isEqualTo(LocalTime.of(8, 0));
        assertThat(result.getEndTime()).isEqualTo(LocalTime.of(9, 0));
        assertThat(result.getRoom()).isEqualTo("B2");
        verify(scheduleEntryRepository).save(result);
    }

    @Test
    void updateScheduleEntry_EntryNotFound_Throws() {
        when(scheduleEntryRepository.findById(10L)).thenReturn(Optional.empty());

        ScheduleEntry updated = new ScheduleEntry();

        assertThatThrownBy(() -> adminScheduleService.updateScheduleEntry(10L, 1L, updated))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Schedule entry with ID 10 not found.");
    }

    @Test
    void updateScheduleEntry_CourseNotFound_Throws() {
        when(scheduleEntryRepository.findById(10L)).thenReturn(Optional.of(entry));
        when(courseRepository.findById(1L)).thenReturn(Optional.empty());

        ScheduleEntry updated = new ScheduleEntry();

        assertThatThrownBy(() -> adminScheduleService.updateScheduleEntry(10L, 1L, updated))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Course with ID 1 not found.");
    }

    // ---------- deleteScheduleEntry ----------

    @Test
    void deleteScheduleEntry_Existing_DeletesById() {
        when(scheduleEntryRepository.existsById(10L)).thenReturn(true);

        adminScheduleService.deleteScheduleEntry(10L);

        verify(scheduleEntryRepository).deleteById(10L);
    }

    @Test
    void deleteScheduleEntry_NotExisting_Throws() {
        when(scheduleEntryRepository.existsById(10L)).thenReturn(false);

        assertThatThrownBy(() -> adminScheduleService.deleteScheduleEntry(10L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Schedule entry with ID 10 not found.");
        verify(scheduleEntryRepository, never()).deleteById(anyLong());
    }
}