package hr.algebra.uni_course_management.controller.api;

import hr.algebra.uni_course_management.exception.ResourceNotFoundException;
import hr.algebra.uni_course_management.model.ContentType;
import hr.algebra.uni_course_management.model.Course;
import hr.algebra.uni_course_management.model.CourseContent;
import hr.algebra.uni_course_management.service.CourseContentService;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CourseContentRestControllerTest {
    @Mock
    private CourseContentService courseContentService;
    @Mock private
    CourseService courseService;
    @InjectMocks
    private CourseContentRestController controller;
    private Course course;
    private CourseContent content;
    private List<CourseContent> contents;

    @BeforeEach
    void setUp() {
        course = new Course();
        course.setId(1L);

        content = new CourseContent();
        content.setId(10L);
        content.setContentTitle("Test Content");

        contents = List.of(content);
    }

    @Test
    void getCourseContent_empty_returnsNotFound() {
        // Given
        when(courseService.getCourseById(1L)).thenReturn(course);
        when(courseContentService.getAllCourseContents(1L)).thenReturn(List.of());

        // When
        var response = controller.getCourseContent(1L);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertThat(body.get("success")).isEqualTo(false);
        verify(courseContentService).getAllCourseContents(1L);
    }

    @Test
    void getCourseContent_courseNotFound_returnsNotFound() {
        // Given
        when(courseService.getCourseById(999L)).thenThrow(new ResourceNotFoundException("Course not found"));

        // When
        var response = controller.getCourseContent(999L);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(courseService).getCourseById(999L);
    }

    @Test
    void createContent_success_returnsCreated() {
        // Given
        when(courseService.getCourseById(1L)).thenReturn(course);
        when(courseContentService.createContent(eq(1L), any(CourseContent.class))).thenReturn(content);

        // When
        var response = controller.createContent(1L, "New Content", "Content body", ContentType.LECTURE);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertThat(body.get("success")).isEqualTo(true);
        verify(courseContentService).createContent(eq(1L), any(CourseContent.class));
    }

    @Test
    void createContent_courseNotFound_returnsBadRequest() {
        // Given
        when(courseService.getCourseById(999L)).thenThrow(new ResourceNotFoundException("Course not found"));

        // When
        var response = controller.createContent(999L, "Title", "Content", ContentType.LECTURE);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        verify(courseService).getCourseById(999L);
    }

    @Test
    void updateContent_contentNotFound_returnsBadRequest() {
        // Given
        when(courseService.getCourseById(1L)).thenReturn(course);
        when(courseContentService.getContentById(999L)).thenThrow(new ResourceNotFoundException("Content not found"));

        // When
        var response = controller.updateContent(1L, 999L, "Title", null, null, null);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        verify(courseContentService).getContentById(999L);
    }

    @Test
    void deleteContent_success_returnsOk() {
        // Given
        when(courseService.getCourseById(1L)).thenReturn(course);

        // When
        var response = controller.deleteContent(1L, 10L);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertThat(body.get("success")).isEqualTo(true);
        verify(courseContentService).deleteContent(10L);
    }

    @Test
    void deleteContent_courseNotFound_returnsNotFound() {
        // Given
        when(courseService.getCourseById(999L)).thenThrow(new ResourceNotFoundException("Course not found"));

        // When
        var response = controller.deleteContent(999L, 10L);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(courseService).getCourseById(999L);
    }

    @Test
    void togglePublish_success_returnsOk() {
        // Given
        when(courseService.getCourseById(1L)).thenReturn(course);
        content.setIsPublished(false);  // toggle to true
        when(courseContentService.togglePublishStatus(10L)).thenReturn(content);

        // When
        var response = controller.togglePublish(1L, 10L);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertThat(body.get("success")).isEqualTo(true);
        assertThat((String) body.get("message")).contains("published successfully");
        verify(courseContentService).togglePublishStatus(10L);
    }

    @Test
    void togglePublish_contentNotFound_returnsBadRequest() {
        // Given
        when(courseService.getCourseById(1L)).thenReturn(course);
        when(courseContentService.togglePublishStatus(999L))
                .thenThrow(new ResourceNotFoundException("Content not found"));

        // When
        var response = controller.togglePublish(1L, 999L);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        verify(courseContentService).togglePublishStatus(999L);
    }
}