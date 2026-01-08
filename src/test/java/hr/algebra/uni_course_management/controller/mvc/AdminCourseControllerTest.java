package hr.algebra.uni_course_management.controller.mvc;

import hr.algebra.uni_course_management.model.Course;
import hr.algebra.uni_course_management.model.User;
import hr.algebra.uni_course_management.model.UserRole;
import hr.algebra.uni_course_management.repository.UserRepository;
import hr.algebra.uni_course_management.service.CourseService;
import hr.algebra.uni_course_management.service.GradeExportService;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.io.PrintWriter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminCourseControllerTest {
    @Mock(lenient = true)
    private CourseService courseService;
    @Mock(lenient = true)
    private UserRepository userRepository;
    @Mock(lenient = true)
    private GradeExportService gradeExportService;
    @Mock private Model model;
    @Mock private BindingResult bindingResult;
    @InjectMocks private AdminCourseController controller;
    private final List<User> professors = List.of(createProfessor("prof1"), createProfessor("prof2"));
    private final Course testCourse = new Course();

    public AdminCourseControllerTest() {
        testCourse.setId(1L);
        testCourse.setCourseCode("TEST 101");
    }

    @Test
    void getCourses_addsCourses_returnsListView() {
        List<Course> courses = List.of(testCourse);
        when(courseService.getAllCourses()).thenReturn(courses);

        String viewName = controller.getCourses(model);

        assertEquals("admin/courses/list", viewName);
        verify(courseService).getAllCourses();
        verify(model).addAttribute("courses", courses);
    }

    @Test
    void createCourse_valid_returnsRedirect() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(courseService.createCourse(testCourse)).thenReturn(testCourse);

        String viewName = controller.createCourse(testCourse, bindingResult, model);

        assertEquals("redirect:/admin/courses?success=created", viewName);
        verify(courseService).createCourse(testCourse);
    }

    @Test
    void createCourse_validationError_returnsForm() {
        when(bindingResult.hasErrors()).thenReturn(true);

        String viewName = controller.createCourse(testCourse, bindingResult, model);

        assertEquals("admin/courses/create", viewName);
    }

    @Test
    void createCourse_exception_returnsFormWithError() {
        when(bindingResult.hasErrors()).thenReturn(false);
        doThrow(new IllegalArgumentException("Duplicate")).when(courseService).createCourse(any());

        String viewName = controller.createCourse(testCourse, bindingResult, model);

        assertEquals("admin/courses/create", viewName);
        verify(model).addAttribute(eq("errorMessage"), startsWith("Error creating course:"));
    }

    @Test
    void editCourseForm_returnsEditView() {
        when(courseService.getCourseById(1L)).thenReturn(testCourse);

        String viewName = controller.editCourseForm(1L, model);

        assertEquals("admin/courses/edit", viewName);
    }

    @Test
    void editCourse_valid_returnsRedirect() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(courseService.updateCourse(1L, testCourse)).thenReturn(testCourse);

        String viewName = controller.editCourse(1L, testCourse, bindingResult, model);

        assertEquals("redirect:/admin/courses?success=updated", viewName);
    }

    @Test
    void deleteCourse_returnsRedirect() {
        String viewName = controller.deleteCourse(1L);

        assertEquals("redirect:/admin/courses?success=deleted", viewName);
        verify(courseService).deleteCourse(1L);
    }

    @Test
    void exportCourseGrades_success_returnsNull() throws Exception {
        String csvData = "header\nrow1";
        when(gradeExportService.exportGradesCsv(1L)).thenReturn(csvData);
        when(courseService.getCourseById(1L)).thenReturn(testCourse);

        HttpServletResponse response = mock(HttpServletResponse.class);
        PrintWriter writer = mock(PrintWriter.class);
        when(response.getWriter()).thenReturn(writer);

        String result = controller.exportCourseGrades(1L, response);

        verify(response).setContentType("text/csv");
        verify(response).setHeader(eq("Content-Disposition"), contains("course_TEST_101_grades.csv"));
        verify(writer).write(csvData);
        assertNull(result);
    }

    @Test
    void exportCourseGrades_exception_returnsRedirect() throws Exception {
        when(gradeExportService.exportGradesCsv(1L)).thenReturn("data");
        when(courseService.getCourseById(1L)).thenReturn(testCourse);

        HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getWriter()).thenThrow(new RuntimeException("IO fail"));

        String result = controller.exportCourseGrades(1L, response);

        assertEquals("redirect:/admin/courses?error=export_failed", result);
    }

    private User createProfessor(String username) {
        User prof = new User();
        prof.setRole(UserRole.PROFESSOR);
        return prof;
    }
}