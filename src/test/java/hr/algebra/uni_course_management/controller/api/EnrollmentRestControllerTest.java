package hr.algebra.uni_course_management.controller.api;

import hr.algebra.uni_course_management.model.Enrollment;
import hr.algebra.uni_course_management.model.User;
import hr.algebra.uni_course_management.service.EnrollmentService;
import hr.algebra.uni_course_management.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnrollmentRestControllerTest {
    @Mock
    private EnrollmentService enrollmentService;
    @Mock
    private UserService userService;
    @Mock
    private Authentication authentication;
    @InjectMocks
    private EnrollmentRestController controller;
    private User student;
    private List<Enrollment> enrollments;
    private Enrollment enrollment;

    @BeforeEach
    void setUp() {
        student = new User();
        student.setId(1L);
        student.setUsername("student1");

        enrollment = new Enrollment();
        enrollment.setId(1L);

        enrollments = List.of(enrollment);

        when(authentication.getName()).thenReturn("student1");
    }

    @Test
    void getCurrentStudentEnrollments_success_returnsOk() {
        // Given
        when(enrollmentService.findByStudentUsername("student1")).thenReturn(enrollments);

        // When
        var response = controller.getCurrentStudentEnrollments(authentication);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertThat(body.get("success")).isEqualTo(true);
        @SuppressWarnings("unchecked")
        List<?> data = (List<?>) body.get("data");
        assertThat(data).hasSize(1);
        verify(enrollmentService).findByStudentUsername("student1");
    }

    @Test
    void getCurrentStudentEnrollments_error_returnsInternalServerError() {
        // Given
        when(enrollmentService.findByStudentUsername("student1"))
                .thenThrow(new RuntimeException("Database error"));

        // When
        var response = controller.getCurrentStudentEnrollments(authentication);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertThat(body.get("success")).isEqualTo(false);
        verify(enrollmentService).findByStudentUsername("student1");
    }

    @Test
    void enrollInCourse_success_returnsCreated() {
        // Given
        when(userService.findByUsername("student1")).thenReturn(Optional.of(student));
        when(enrollmentService.enrollStudent(1L, 10L)).thenReturn(enrollment);

        // When
        var response = controller.enrollInCourse(10L, authentication);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertThat(body.get("success")).isEqualTo(true);
        verify(enrollmentService).enrollStudent(1L, 10L);
    }

    @Test
    void enrollInCourse_studentNotFound_returnsNotFound() {
        // Given
        when(userService.findByUsername("student1")).thenReturn(Optional.empty());

        // When
        var response = controller.enrollInCourse(10L, authentication);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertThat(body.get("success")).isEqualTo(false);
        verify(userService).findByUsername("student1");
        verifyNoInteractions(enrollmentService);
    }

    @Test
    void enrollInCourse_enrollmentError_returnsBadRequest() {
        // Given
        when(userService.findByUsername("student1")).thenReturn(Optional.of(student));
        when(enrollmentService.enrollStudent(1L, 10L))
                .thenThrow(new RuntimeException("Course full"));

        // When
        var response = controller.enrollInCourse(10L, authentication);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        verify(enrollmentService).enrollStudent(1L, 10L);
    }

    @Test
    void dropFromCourse_success_returnsOk() {
        // Given
        when(userService.findByUsername("student1")).thenReturn(Optional.of(student));

        // When
        var response = controller.dropFromCourse(10L, authentication);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertThat(body.get("success")).isEqualTo(true);
        verify(enrollmentService).dropStudent(1L, 10L);
    }

    @Test
    void dropFromCourse_studentNotFound_returnsNotFound() {
        // Given
        when(userService.findByUsername("student1")).thenReturn(Optional.empty());

        // When
        var response = controller.dropFromCourse(10L, authentication);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(userService).findByUsername("student1");
        verifyNoInteractions(enrollmentService);
    }

    @Test
    void dropFromCourse_error_returnsInternalServerError() {
        // Given
        when(userService.findByUsername("student1")).thenReturn(Optional.of(student));
        doThrow(new RuntimeException("Cannot drop")).when(enrollmentService).dropStudent(1L, 10L);

        // When
        var response = controller.dropFromCourse(10L, authentication);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        verify(enrollmentService).dropStudent(1L, 10L);
    }
}