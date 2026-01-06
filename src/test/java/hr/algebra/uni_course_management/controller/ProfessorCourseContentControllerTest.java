package hr.algebra.uni_course_management.controller;

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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfessorCourseContentControllerTest {
    @Mock
    private CourseService courseService;
    @Mock
    private CourseContentService courseContentService;
    @Mock private BindingResult bindingResult;
    @Mock private RedirectAttributes redirectAttributes;
    @InjectMocks
    private ProfessorCourseContentController controller;
    private MockMvc mockMvc;
    @Mock
    private Model model;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void viewCourseContent_addsAttributes_returnsListView() {
        Long courseId = 1L;
        Course course = new Course();
        course.setId(courseId);
        List<CourseContent> contents = List.of(new CourseContent());

        when(courseService.getCourseById(courseId)).thenReturn(course);
        when(courseContentService.getAllCourseContents(courseId)).thenReturn(contents);

        String viewName = controller.viewCourseContent(courseId, model);

        assertThat(viewName).isEqualTo("professor/content/list");
        verify(courseService).getCourseById(courseId);
        verify(courseContentService).getAllCourseContents(courseId);

        verify(model).addAttribute("courseContent", contents);
        verify(model).addAttribute("course", course);
        verify(model).addAttribute("contentTypes", ContentType.values());
    }

    @Test
    void showCreateContentForm_addsEmptyContent_returnsCreateView() {
        Long courseId = 1L;
        Course course = new Course();
        course.setId(courseId);

        when(courseService.getCourseById(courseId)).thenReturn(course);
        String viewName = controller.showCreateContentForm(courseId, model);

        assertThat(viewName).isEqualTo("professor/content/create");
        verify(courseService).getCourseById(courseId);
        verify(model).addAttribute(eq("courseContent"), any(CourseContent.class));
        verify(model).addAttribute(eq("course"), eq(course));
        verify(model).addAttribute(eq("contentTypes"), eq(ContentType.values()));
    }

    @Test
    void createContent_validContent_redirectsToList() {
        Long courseId = 1L;
        CourseContent content = new CourseContent();
        BindingResult bindingResult = mock(BindingResult.class);
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        when(bindingResult.hasErrors()).thenReturn(false);

        String viewName = controller.createContent(courseId, content, bindingResult,
                redirectAttributes, mock(Model.class));

        assert viewName.equals("redirect:/professor/courses/" + courseId + "/content");
        verify(courseContentService).createContent(courseId, content);
        verify(redirectAttributes).addFlashAttribute(eq("successMessage"), eq("Content created successfully!"));
    }

    @Test
    void createContent_invalidContent_returnsCreateView() {
        Long courseId = 1L;
        CourseContent content = new CourseContent();
        Course course = new Course();

        when(bindingResult.hasErrors()).thenReturn(true);  // Field mock
        when(courseService.getCourseById(courseId)).thenReturn(course);

        String viewName = controller.createContent(courseId, content, bindingResult,
                redirectAttributes, model);

        assertThat(viewName).isEqualTo("professor/content/create");
        verify(courseService).getCourseById(courseId);
        verify(model).addAttribute("course", course);
        verify(model).addAttribute("contentTypes", ContentType.values());
        verifyNoInteractions(courseContentService);
    }

    @Test
    void showEditContentForm_addsContent_returnsEditView() {
        Long courseId = 1L;
        Long contentId = 10L;
        CourseContent content = new CourseContent();
        content.setId(contentId);
        Course course = new Course();
        course.setId(courseId);

        when(courseContentService.getContentById(contentId)).thenReturn(content);
        when(courseService.getCourseById(courseId)).thenReturn(course);

        String viewName = controller.showEditContentForm(courseId, contentId, model);

        assertThat(viewName).isEqualTo("professor/content/edit");
        verify(courseContentService).getContentById(contentId);
        verify(courseService).getCourseById(courseId);

        verify(model).addAttribute("courseContent", content);
        verify(model).addAttribute("course", course);
        verify(model).addAttribute("contentTypes", ContentType.values());
    }

    @Test
    void updateContent_validContent_redirectsToList() {
        Long courseId = 1L;
        Long contentId = 10L;
        CourseContent content = new CourseContent();
        BindingResult bindingResult = mock(BindingResult.class);
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        when(bindingResult.hasErrors()).thenReturn(false);

        String viewName = controller.updateContent(courseId, contentId, content, bindingResult,
                redirectAttributes, mock(Model.class));

        assert viewName.equals("redirect:/professor/courses/" + courseId + "/content");
        verify(courseContentService).updateContent(contentId, content);
        verify(redirectAttributes).addFlashAttribute(eq("successMessage"), eq("Content updated successfully!"));
    }

    @Test
    void updateContent_invalidContent_returnsEditView() {
        Long courseId = 1L;
        Long contentId = 10L;
        CourseContent content = new CourseContent();
        Course course = new Course();

        when(bindingResult.hasErrors()).thenReturn(true);
        when(courseService.getCourseById(courseId)).thenReturn(course);

        String viewName = controller.updateContent(courseId, contentId, content, bindingResult,
                redirectAttributes, model);

        assertThat(viewName).isEqualTo("professor/content/edit");
        verify(courseService).getCourseById(courseId);

        verify(model).addAttribute("course", course);
        verify(model).addAttribute("contentTypes", ContentType.values());

        verifyNoInteractions(courseContentService);
    }

    @Test
    void deleteContent_redirectsToList() {
        Long courseId = 1L;
        Long contentId = 10L;
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        String viewName = controller.deleteContent(courseId, contentId, redirectAttributes);

        assert viewName.equals("redirect:/professor/courses/" + courseId + "/content");
        verify(courseContentService).deleteContent(contentId);
        verify(redirectAttributes).addFlashAttribute(eq("successMessage"), eq("Content deleted successfully!"));
    }

    @Test
    void togglePublishStatus_published_redirectsWithPublishedMessage() {
        Long courseId = 1L;
        Long contentId = 10L;
        CourseContent content = new CourseContent();
        content.setIsPublished(true);
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        when(courseContentService.togglePublishStatus(contentId)).thenReturn(content);

        String viewName = controller.togglePublishStatus(courseId, contentId, redirectAttributes);

        assert viewName.equals("redirect:/professor/courses/" + courseId + "/content");
        verify(courseContentService).togglePublishStatus(contentId);
        verify(redirectAttributes).addFlashAttribute(eq("successMessage"), eq("Content published successfully!"));
    }

    @Test
    void togglePublishStatus_unpublished_redirectsWithUnpublishedMessage() {
        Long courseId = 1L;
        Long contentId = 10L;
        CourseContent content = new CourseContent();
        content.setIsPublished(false);
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        when(courseContentService.togglePublishStatus(contentId)).thenReturn(content);

        String viewName = controller.togglePublishStatus(courseId, contentId, redirectAttributes);

        assert viewName.equals("redirect:/professor/courses/" + courseId + "/content");
        verify(courseContentService).togglePublishStatus(contentId);
        verify(redirectAttributes).addFlashAttribute(eq("successMessage"), eq("Content unpublished successfully!"));
    }
}