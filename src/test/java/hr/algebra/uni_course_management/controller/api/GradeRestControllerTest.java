package hr.algebra.uni_course_management.controller.api;

import hr.algebra.uni_course_management.exception.ResourceNotFoundException;
import hr.algebra.uni_course_management.model.Grade;
import hr.algebra.uni_course_management.service.GradeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GradeRestControllerTest {
    @Mock
    private GradeService gradeService;
    @InjectMocks
    private GradeRestController controller;
    private Grade grade;
    private List<Grade> grades;

    @BeforeEach
    void setUp() {
        grade = new Grade();
        grade.setId(1L);
        grade.setGradeValue(95);
        grades = List.of(grade);
    }

    @Test
    void assignGrade_success_returnsCreated() {
        // Given
        when(gradeService.assignGrade(10L, 95)).thenReturn(grade);

        // When
        var response = controller.assignGrade(10L, 95);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertThat(body.get("success")).isEqualTo(true);
        @SuppressWarnings("unchecked")
        Grade data = (Grade) body.get("data");
        assertThat(data.getGradeValue()).isEqualTo(95);
        verify(gradeService).assignGrade(10L, 95);
    }

    @Test
    void assignGrade_enrollmentNotFound_returnsNotFound() {
        // Given
        when(gradeService.assignGrade(999L, 95))
                .thenThrow(new ResourceNotFoundException("Enrollment not found"));

        // When
        var response = controller.assignGrade(999L, 95);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertThat(body.get("success")).isEqualTo(false);
        verify(gradeService).assignGrade(999L, 95);
    }

    @Test
    void assignGrade_runtimeError_returnsBadRequest() {
        // Given
        when(gradeService.assignGrade(10L, 95))
                .thenThrow(new RuntimeException("Invalid grade"));

        // When
        var response = controller.assignGrade(10L, 95);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        verify(gradeService).assignGrade(10L, 95);
    }

    @Test
    void getGrades_byCourse_success_returnsOk() {
        // Given
        when(gradeService.getGradesByCourse(1L)).thenReturn(grades);

        // When
        var response = controller.getGrades(1L, null);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertThat(body.get("success")).isEqualTo(true);
        @SuppressWarnings("unchecked")
        List<Grade> data = (List<Grade>) body.get("data");
        assertThat(data).hasSize(1);
        verify(gradeService).getGradesByCourse(1L);
    }

    @Test
    void getGrades_byStudent_success_returnsOk() {
        // Given
        when(gradeService.getGradesByStudent(2L)).thenReturn(grades);

        // When
        var response = controller.getGrades(null, 2L);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertThat(body.get("success")).isEqualTo(true);
        @SuppressWarnings("unchecked")
        List<Grade> data = (List<Grade>) body.get("data");
        assertThat(data).hasSize(1);
        verify(gradeService).getGradesByStudent(2L);
    }

    @Test
    void getGrades_noParams_returnsBadRequest() {
        // When
        var response = controller.getGrades(null, null);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertThat(body.get("success")).isEqualTo(false);
        assertThat((String) body.get("message")).contains("courseId or studentId");
        verifyNoInteractions(gradeService);
    }

    @Test
    void getGrades_courseNotFound_returnsNotFound() {
        // Given
        when(gradeService.getGradesByCourse(999L))
                .thenThrow(new ResourceNotFoundException("Course not found"));

        // When
        var response = controller.getGrades(999L, null);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(gradeService).getGradesByCourse(999L);
    }

    @Test
    void getGrades_studentNotFound_returnsNotFound() {
        // Given
        when(gradeService.getGradesByStudent(999L))
                .thenThrow(new ResourceNotFoundException("Student not found"));

        // When
        var response = controller.getGrades(null, 999L);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(gradeService).getGradesByStudent(999L);
    }
}