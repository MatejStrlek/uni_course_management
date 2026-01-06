package hr.algebra.uni_course_management.controller.api;

import hr.algebra.uni_course_management.jwt.ApiResponse;
import hr.algebra.uni_course_management.model.Course;
import hr.algebra.uni_course_management.service.CourseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseRestControllerTest {
    @Mock
    private CourseService courseService;
    @InjectMocks
    private CourseRestController controller;
    private Course course;
    private List<Course> courses;

    @BeforeEach
    void setUp() {
        course = new Course();
        course.setId(1L);
        course.setCourseName("Test Course");
        course.setCourseCode("TC101");
        courses = List.of(course);
    }

    @Test
    void getAllCourses_success_returnsOk() {
        // Given
        when(courseService.getAllCourses()).thenReturn(courses);

        // When
        var response = controller.getAllCourses();

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        @SuppressWarnings("unchecked")
        ApiResponse<List<Course>> apiResponse = (ApiResponse<List<Course>>) response.getBody();
        assertThat(apiResponse.getData()).hasSize(1);
        assertThat(apiResponse.getMessage()).isEqualTo("Courses retrieved successfully");
        verify(courseService).getAllCourses();
    }

    @Test
    void getCourseById_success_returnsOk() {
        // Given
        when(courseService.getCourseById(1L)).thenReturn(course);

        // When
        var response = controller.getCourseById(1L);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertThat(body.get("success")).isEqualTo(true);
        @SuppressWarnings("unchecked")
        Course data = (Course) body.get("data");
        assertThat(data.getCourseName()).isEqualTo("Test Course");
        verify(courseService).getCourseById(1L);
    }

    @Test
    void createCourse_success_returnsCreated() {
        // Given
        Course newCourse = new Course();
        newCourse.setCourseName("New Course");
        when(courseService.createCourse(any(Course.class))).thenReturn(newCourse);

        // When
        var response = controller.createCourse(newCourse);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        @SuppressWarnings("unchecked")
        ApiResponse<Course> apiResponse = (ApiResponse<Course>) response.getBody();
        assertThat(apiResponse.getData().getCourseName()).isEqualTo("New Course");
        verify(courseService).createCourse(any(Course.class));
    }

    @Test
    void updateCourse_success_returnsOk() {
        // Given
        Course existingCourse = new Course();
        existingCourse.setId(1L);
        existingCourse.setCourseName("Old Name");
        Course updateDetails = new Course();
        updateDetails.setCourseName("Updated Name");
        when(courseService.getCourseById(1L)).thenReturn(existingCourse);
        when(courseService.updateCourse(1L, existingCourse)).thenReturn(existingCourse);

        // When
        var response = controller.updateCourse(1L, updateDetails);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        Object body = response.getBody();
        if (body instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> mapBody = (Map<String, Object>) body;
            assertThat(mapBody.get("success")).isEqualTo(true);
        } else {
            assertThat(body).isNotNull();
        }

        verify(courseService).updateCourse(1L, existingCourse);
    }

    @Test
    void updateCourse_notFound_returnsNotFound() {
        // Given
        Course updateDetails = new Course();
        when(courseService.getCourseById(999L)).thenThrow(new RuntimeException("Course not found"));

        // When
        var response = controller.updateCourse(999L, updateDetails);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        @SuppressWarnings("unchecked")
        ApiResponse<?> apiResponse = (ApiResponse<?>) response.getBody();
        assertThat(apiResponse.getMessage()).contains("Course not found");
        verify(courseService).getCourseById(999L);
    }

    @Test
    void deleteCourse_success_returnsOk() {
        // Given
        doNothing().when(courseService).deleteCourse(1L);

        // When
        var response = controller.deleteCourse(1L);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertThat(body.get("success")).isEqualTo(true);
        verify(courseService).deleteCourse(1L);
    }
}