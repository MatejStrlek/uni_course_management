package hr.algebra.uni_course_management.controller.api;

import hr.algebra.uni_course_management.model.Course;
import hr.algebra.uni_course_management.model.ScheduleEntry;
import hr.algebra.uni_course_management.service.AdminScheduleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScheduleRestControllerTest {
    @Mock
    private AdminScheduleService adminScheduleService;
    @InjectMocks
    private ScheduleRestController controller;
    private ScheduleEntry sampleSchedule;

    @BeforeEach
    void setUp() {
        Course sampleCourse = new Course();
        sampleCourse.setId(1L);

        sampleSchedule = new ScheduleEntry();
        sampleSchedule.setId(1L);
        sampleSchedule.setDayOfWeek(DayOfWeek.MONDAY);
        sampleSchedule.setStartTime(LocalTime.of(9, 0));
        sampleSchedule.setEndTime(LocalTime.of(11, 0));
        sampleSchedule.setRoom("A101");
        sampleSchedule.setCourse(sampleCourse);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createSchedule_success_returnsCreated() {
        // Given
        when(adminScheduleService.createScheduleEntry(eq(1L), any(ScheduleEntry.class)))
                .thenReturn(sampleSchedule);

        // When
        ResponseEntity<?> response = controller.createSchedule(1L, DayOfWeek.MONDAY,
                LocalTime.of(9, 0), LocalTime.of(11, 0), "A101");

        // Then
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(body.get("success")).isEqualTo(true);
        assertThat(body.get("message")).isEqualTo("Schedule entry created successfully");
        assertThat(((ScheduleEntry) body.get("data")).getRoom()).isEqualTo("A101");
        verify(adminScheduleService).createScheduleEntry(eq(1L), any(ScheduleEntry.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createSchedule_illegalArgument_returnsNotFound() {
        // Given
        when(adminScheduleService.createScheduleEntry(anyLong(), any(ScheduleEntry.class)))
                .thenThrow(new IllegalArgumentException("Course not found"));

        // When
        ResponseEntity<?> response = controller.createSchedule(999L, DayOfWeek.MONDAY,
                LocalTime.of(9, 0), LocalTime.of(11, 0), "A101");

        // Then
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(body.get("success")).isEqualTo(false);
        assertThat(body.get("message")).isEqualTo("Course not found");
        verify(adminScheduleService).createScheduleEntry(eq(999L), any(ScheduleEntry.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createSchedule_runtimeException_returnsBadRequest() {
        // Given
        when(adminScheduleService.createScheduleEntry(anyLong(), any(ScheduleEntry.class)))
                .thenThrow(new RuntimeException("Invalid time range"));

        // When
        ResponseEntity<?> response = controller.createSchedule(1L, DayOfWeek.MONDAY,
                LocalTime.of(9, 0), LocalTime.of(11, 0), "A101");

        // Then
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(body.get("success")).isEqualTo(false);
        assertThat(body.get("message")).isEqualTo("Invalid time range");
        verify(adminScheduleService).createScheduleEntry(eq(1L), any(ScheduleEntry.class));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void getCourseSchedule_success_returnsOk() {
        // Given
        when(adminScheduleService.findAllScheduleEntriesSorted()).thenReturn(List.of(sampleSchedule));

        // When
        ResponseEntity<?> response = controller.getCourseSchedule(1L);

        // Then
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(body.get("success")).isEqualTo(true);
        assertThat(((List<?>) body.get("data"))).hasSize(1);
        verify(adminScheduleService).findAllScheduleEntriesSorted();
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void getCourseSchedule_noSchedules_returnsNotFound() {
        // Given
        when(adminScheduleService.findAllScheduleEntriesSorted()).thenReturn(Collections.emptyList());

        // When
        ResponseEntity<?> response = controller.getCourseSchedule(999L);

        // Then
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(body.get("success")).isEqualTo(false);
        assertThat(body.get("message")).isEqualTo("No schedule found for the specified course");
        verify(adminScheduleService).findAllScheduleEntriesSorted();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllSchedules_success_returnsOk() {
        // Given
        when(adminScheduleService.findAllScheduleEntriesSorted()).thenReturn(List.of(sampleSchedule));

        // When
        ResponseEntity<?> response = controller.getAllSchedules();

        // Then
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(body.get("success")).isEqualTo(true);
        assertThat(((List<?>) body.get("data"))).hasSize(1);
        verify(adminScheduleService).findAllScheduleEntriesSorted();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateSchedule_success_returnsOk() {
        // Given
        when(adminScheduleService.updateScheduleEntry(eq(1L), eq(1L), any(ScheduleEntry.class)))
                .thenReturn(sampleSchedule);

        // When
        ResponseEntity<?> response = controller.updateSchedule(1L, 1L, DayOfWeek.TUESDAY,
                LocalTime.of(10, 0), LocalTime.of(12, 0), "B202");

        // Then
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(body.get("success")).isEqualTo(true);
        assertThat(body.get("message")).isEqualTo("Schedule entry updated successfully");
        assertThat(((ScheduleEntry) body.get("data")).getId()).isEqualTo(1L);
        verify(adminScheduleService).updateScheduleEntry(eq(1L), eq(1L), any(ScheduleEntry.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateSchedule_notFound_returnsNotFound() {
        // Given
        when(adminScheduleService.updateScheduleEntry(eq(999L), anyLong(), any(ScheduleEntry.class)))
                .thenThrow(new IllegalArgumentException("Schedule not found"));

        // When
        ResponseEntity<?> response = controller.updateSchedule(999L, 1L, DayOfWeek.MONDAY,
                LocalTime.of(9, 0), LocalTime.of(11, 0), "A101");

        // Then
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(body.get("success")).isEqualTo(false);
        assertThat(body.get("message")).isEqualTo("Schedule not found");
        verify(adminScheduleService).updateScheduleEntry(eq(999L), eq(1L), any(ScheduleEntry.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteSchedule_success_returnsOk() {
        // Given
        doNothing().when(adminScheduleService).deleteScheduleEntry(1L);

        // When
        ResponseEntity<?> response = controller.deleteSchedule(1L);

        // Then
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(body.get("success")).isEqualTo(true);
        assertThat(body.get("message")).isEqualTo("Schedule entry deleted successfully");
        verify(adminScheduleService).deleteScheduleEntry(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteSchedule_notFound_returnsNotFound() {
        // Given
        doThrow(new IllegalArgumentException("Schedule not found"))
                .when(adminScheduleService).deleteScheduleEntry(999L);

        // When
        ResponseEntity<?> response = controller.deleteSchedule(999L);

        // Then
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(body.get("success")).isEqualTo(false);
        assertThat(body.get("message")).isEqualTo("Schedule not found");
        verify(adminScheduleService).deleteScheduleEntry(999L);
    }
}