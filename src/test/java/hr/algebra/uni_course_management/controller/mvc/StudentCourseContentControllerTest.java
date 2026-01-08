package hr.algebra.uni_course_management.controller.mvc;

import hr.algebra.uni_course_management.model.Course;
import hr.algebra.uni_course_management.model.CourseContent;
import hr.algebra.uni_course_management.service.CourseContentService;
import hr.algebra.uni_course_management.service.CourseService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentCourseContentControllerTest {
    @Mock
    private CourseService courseService;
    @Mock
    private CourseContentService courseContentService;
    @Mock
    private Model model;
    @InjectMocks
    private StudentCourseContentController controller;

    @Test
    void listContent_returnsListViewWithCourseAndContents() {
        Long courseId = 1L;

        Course course = new Course();
        course.setId(courseId);
        course.setCourseName("Test course");

        CourseContent content1 = new CourseContent();
        content1.setId(10L);
        CourseContent content2 = new CourseContent();
        content2.setId(20L);

        when(courseService.getCourseById(courseId)).thenReturn(course);
        when(courseContentService.getPublishedCourseContents(courseId))
                .thenReturn(List.of(content1, content2));

        String viewName = controller.listContent(courseId, model);

        assertThat(viewName).isEqualTo("student/content/list");
        verify(courseService).getCourseById(courseId);
        verify(courseContentService).getPublishedCourseContents(courseId);
        verify(model).addAttribute("course", course);
        verify(model).addAttribute(eq("contents"), argThat(list ->
                list instanceof List<?> l && l.size() == 2
        ));
    }

    @Test
    void viewContent_publishedContent_returnsViewTemplate() {
        Long courseId = 1L;
        Long contentId = 10L;

        Course course = new Course();
        course.setId(courseId);

        CourseContent content = new CourseContent();
        content.setId(contentId);
        content.setIsPublished(true);

        when(courseService.getCourseById(courseId)).thenReturn(course);
        when(courseContentService.getContentById(contentId)).thenReturn(content);

        String viewName = controller.viewContent(courseId, contentId, model);

        assertThat(viewName).isEqualTo("student/content/view");
        verify(courseService).getCourseById(courseId);
        verify(courseContentService).getContentById(contentId);
        verify(model).addAttribute("course", course);
        verify(model).addAttribute("content", content);
        verify(model, never()).addAttribute(eq("errorMessage"), any());
    }

    @Test
    void viewContent_nullContent_returnsListWithError() {
        Long courseId = 1L;
        Long contentId = 10L;

        Course course = new Course();
        course.setId(courseId);

        when(courseService.getCourseById(courseId)).thenReturn(course);
        when(courseContentService.getContentById(contentId)).thenReturn(null);

        String viewName = controller.viewContent(courseId, contentId, model);

        assertThat(viewName).isEqualTo("student/content/list");
        verify(model).addAttribute("course", course);
        verify(model).addAttribute("errorMessage", "Content not found or not published.");
    }

    @Test
    void viewContent_unpublishedContent_returnsListWithError() {
        Long courseId = 1L;
        Long contentId = 10L;

        Course course = new Course();
        course.setId(courseId);

        CourseContent content = new CourseContent();
        content.setId(contentId);
        content.setIsPublished(false);

        when(courseService.getCourseById(courseId)).thenReturn(course);
        when(courseContentService.getContentById(contentId)).thenReturn(content);

        String viewName = controller.viewContent(courseId, contentId, model);

        assertThat(viewName).isEqualTo("student/content/list");
        verify(model).addAttribute("course", course);
        verify(model).addAttribute("errorMessage", "Content not found or not published.");
        verify(model, never()).addAttribute(eq("content"), any());
    }
}