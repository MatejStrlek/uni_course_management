package hr.algebra.uni_course_management.controller.mvc;

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
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminCourseContentControllerTest {
    @Mock(lenient = true) private CourseService courseService;
    @Mock(lenient = true) private CourseContentService courseContentService;
    @Mock private Model model;
    @Mock private BindingResult bindingResult;
    @Mock private RedirectAttributes redirectAttributes;
    @InjectMocks private AdminCourseContentController controller;
    private Course testCourse;
    private CourseContent testContent;

    @BeforeEach
    void setUp() {
        testCourse = new Course();
        testCourse.setId(1L);
        testCourse.setCourseCode("TEST101");

        testContent = new CourseContent();
        testContent.setId(10L);

        when(courseService.getCourseById(1L)).thenReturn(testCourse);
    }

    @Test
    void viewCourseContent_addsAttributes_returnsListView() {
        List<CourseContent> contents = List.of(testContent);
        when(courseContentService.getAllCourseContents(1L)).thenReturn(contents);

        String viewName = controller.viewCourseContent(1L, model);

        assertThat(viewName).isEqualTo("admin/content/list");
        verify(courseContentService).getAllCourseContents(1L);
        verify(model).addAttribute(eq("contents"), eq(contents));
        verify(model).addAttribute(eq("course"), eq(testCourse));
        verify(model).addAttribute(eq("contentTypes"), eq(ContentType.values()));
        verify(model).addAttribute(eq("publishedCount"), eq(0L));
        verify(model).addAttribute(eq("draftCount"), eq(1L));
    }

    @Test
    void showCreateContentForm_addsAttributes_returnsCreateView() {
        String viewName = controller.showCreateContentForm(1L, model);

        assertThat(viewName).isEqualTo("admin/content/create");
        verify(model).addAttribute(anyString(), any(CourseContent.class));
        verify(model).addAttribute(eq("course"), eq(testCourse));
        verify(model).addAttribute(eq("contentTypes"), eq(ContentType.values()));
    }

    @Test
    void createContent_validationError_returnsCreateForm() {
        when(bindingResult.hasErrors()).thenReturn(true);

        String viewName = controller.createContent(1L, testContent, bindingResult, redirectAttributes, model);

        assertThat(viewName).isEqualTo("admin/content/create");
        verifyNoInteractions(courseContentService);
    }

    @Test
    void showEditContentForm_addsAttributes_returnsEditView() {
        when(courseContentService.getContentById(10L)).thenReturn(testContent);

        String viewName = controller.showEditContentForm(1L, 10L, model);

        assertThat(viewName).isEqualTo("admin/content/edit");
        verify(courseContentService).getContentById(10L);
        verify(model).addAttribute(eq("courseContent"), eq(testContent));
        verify(model).addAttribute(eq("course"), eq(testCourse));
    }

    @Test
    void updateContent_validationError_returnsEditForm() {
        when(bindingResult.hasErrors()).thenReturn(true);

        String viewName = controller.updateContent(1L, 10L, testContent, bindingResult, redirectAttributes, model);

        assertThat(viewName).isEqualTo("admin/content/edit");
        verifyNoInteractions(courseContentService);
    }

    @Test
    void togglePublishStatus_toPublished_returnsRedirect() {
        CourseContent preToggleContent = new CourseContent();
        preToggleContent.setIsPublished(false);

        CourseContent postToggleContent = new CourseContent();
        postToggleContent.setIsPublished(true);

        when(courseContentService.togglePublishStatus(10L)).thenReturn(postToggleContent);

        String viewName = controller.togglePublishStatus(1L, 10L, redirectAttributes);

        assertThat(viewName).startsWith("redirect:/admin/courses/1/content");
        verify(courseContentService).togglePublishStatus(10L);
        verify(redirectAttributes).addFlashAttribute(eq("successMessage"), eq("Content published successfully!"));
    }

    @Test
    void togglePublishStatus_toUnpublished_returnsRedirect() {
        CourseContent postToggleContent = new CourseContent();
        postToggleContent.setIsPublished(false); // Service toggle mutates to false

        when(courseContentService.togglePublishStatus(10L)).thenReturn(postToggleContent);

        String viewName = controller.togglePublishStatus(1L, 10L, redirectAttributes);

        assertThat(viewName).startsWith("redirect:/admin/courses/1/content");
        verify(courseContentService).togglePublishStatus(10L);
        verify(redirectAttributes).addFlashAttribute(eq("successMessage"), eq("Content unpublished successfully!"));
    }
}