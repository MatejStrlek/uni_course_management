package hr.algebra.uni_course_management.controller.mvc;

import hr.algebra.uni_course_management.model.Course;
import hr.algebra.uni_course_management.model.Enrollment;
import hr.algebra.uni_course_management.model.Grade;
import hr.algebra.uni_course_management.model.User;
import hr.algebra.uni_course_management.service.*;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.PrintWriter;
import java.security.Principal;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfessorCourseControllerTest {
    @Mock
    private UserService userService;
    @Mock
    private CourseService courseService;
    @Mock
    private EnrollmentService enrollmentService;
    @Mock
    private GradeService gradeService;
    @Mock
    private GradeExportService gradeExportService;
    @Mock
    private Model model;
    @Mock
    private Principal principal;
    @Mock
    private RedirectAttributes redirectAttributes;
    @Mock
    private HttpServletResponse response;
    @InjectMocks
    private ProfessorCourseController controller;
    private User professor;

    @BeforeEach
    void setUp() {
        professor = new User();
        professor.setId(1L);
        professor.setEmail("prof@test.com");

        when(principal.getName()).thenReturn(professor.getEmail());
        when(userService.getCurrentUser(professor.getEmail())).thenReturn(professor);
    }

    @Test
    void professorCourses_setsEnrolledStudentsAndReturnsListView() {
        Course c1 = new Course();
        c1.setId(10L);
        Course c2 = new Course();
        c2.setId(20L);

        when(courseService.getCoursesByProfessorId(1L))
                .thenReturn(List.of(c1, c2));
        when(enrollmentService.getActiveEnrollmentsForCourse(10L))
                .thenReturn(List.of(new Enrollment(), new Enrollment())); // 2 students
        when(enrollmentService.getActiveEnrollmentsForCourse(20L))
                .thenReturn(List.of(new Enrollment())); // 1 student

        String viewName = controller.professorCourses(model, principal);

        assertThat(viewName).isEqualTo("professor/courses/list");
        assertThat(c1.getEnrolledStudents()).isEqualTo(2);
        assertThat(c2.getEnrolledStudents()).isEqualTo(1);

        verify(model).addAttribute("courses", List.of(c1, c2));
        verify(courseService).getCoursesByProfessorId(1L);
        verify(enrollmentService).getActiveEnrollmentsForCourse(10L);
        verify(enrollmentService).getActiveEnrollmentsForCourse(20L);
    }

    @Test
    void viewCourseStudents_unauthorizedProfessor_redirectsWithError() {
        Course course = new Course();
        course.setId(10L);

        User otherProf = new User();
        otherProf.setId(99L);
        course.setProfessor(otherProf);

        when(courseService.getCourseById(10L)).thenReturn(course);

        String viewName = controller.viewCourseStudents(10L, model, principal);

        assertThat(viewName).isEqualTo("redirect:/professor/courses?error=unauthorized");
        verify(enrollmentService, never()).getActiveEnrollmentsForCourse(anyLong());
        verify(model, never()).addAttribute(eq("enrollments"), any());
    }

    @Test
    void viewCourseStudents_authorizedProfessor_addsEnrollmentsWithGrades() {
        Course course = new Course();
        course.setId(10L);
        course.setProfessor(professor);

        Enrollment e1 = new Enrollment();
        e1.setId(100L);
        Enrollment e2 = new Enrollment();
        e2.setId(200L);

        Grade grade4 = new Grade();
        grade4.setGradeValue(4);
        Grade grade5 = new Grade();
        grade5.setGradeValue(5);

        when(courseService.getCourseById(10L)).thenReturn(course);
        when(enrollmentService.getActiveEnrollmentsForCourse(10L))
                .thenReturn(List.of(e1, e2));
        when(gradeService.getGradeForEnrollment(100L)).thenReturn(grade5);
        when(gradeService.getGradeForEnrollment(200L)).thenReturn(grade4);

        String viewName = controller.viewCourseStudents(10L, model, principal);

        assertThat(viewName).isEqualTo("professor/courses/students");
        assertThat(e1.getTempGrade().getGradeValue()).isEqualTo(5);
        assertThat(e2.getTempGrade().getGradeValue()).isEqualTo(4);

        verify(model).addAttribute("course", course);
        verify(model).addAttribute(eq("enrollments"), argThat(list ->
                list instanceof List<?> l && l.size() == 2
        ));
    }

    @Test
    void exportCourseGrades_unauthorized_setsForbiddenAndReturns() {
        Course course = new Course();
        course.setId(10L);

        User otherProf = new User();
        otherProf.setId(99L);
        course.setProfessor(otherProf);

        when(courseService.getCourseById(10L)).thenReturn(course);
        controller.exportCourseGrades(10L, response, principal);

        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        verify(gradeExportService, never()).exportGradesCsv(anyLong());
    }

    @Test
    void exportCourseGrades_authorized_writesCsvAndSetsHeaders() throws Exception {
        Course course = new Course();
        course.setId(10L);
        course.setProfessor(professor);
        course.setCourseCode("ALG 101");

        when(courseService.getCourseById(10L)).thenReturn(course);
        when(gradeExportService.exportGradesCsv(10L)).thenReturn("csv,data");

        PrintWriter writer = mock(PrintWriter.class);
        when(response.getWriter()).thenReturn(writer);

        controller.exportCourseGrades(10L, response, principal);

        verify(gradeExportService).exportGradesCsv(10L);
        verify(response).setContentType("text/csv");
        verify(response).setHeader(
                eq("Content-Disposition"),
                eq("attachment; filename=\"course_ALG_101_grades.csv\"")
        );
        verify(writer).write("csv,data");
        verify(writer).flush();
    }
}