package hr.algebra.uni_course_management.controller;

import hr.algebra.uni_course_management.model.*;
import hr.algebra.uni_course_management.service.CourseService;
import hr.algebra.uni_course_management.service.EnrollmentService;
import hr.algebra.uni_course_management.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.security.Principal;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudentCourseControllerTest {
    @Mock
    private CourseService courseService;
    @Mock
    private EnrollmentService enrollmentService;
    @Mock
    private UserService userService;
    @Mock
    private Model model;
    @Mock
    private Principal principal;
    @InjectMocks
    private StudentCourseController controller;

    @BeforeEach
    void setUp() {
        User student = new User();
        student.setId(1L);
        student.setEmail("student@test.com");

        when(principal.getName()).thenReturn(student.getEmail());
        when(userService.getCurrentUser(student.getEmail())).thenReturn(student);
    }

    @Test
    void listAvailableCourses_filtersAlreadyEnrolled() {
        Course enrolledCourse = new Course();
        enrolledCourse.setId(10L);
        enrolledCourse.setIsActive(true);

        Enrollment enrollment = new Enrollment();
        enrollment.setCourse(enrolledCourse);
        enrollment.setStatus(EnrollmentStatus.ENROLLED);

        Course active1 = new Course();
        active1.setId(10L);
        active1.setIsActive(true);

        Course active2 = new Course();
        active2.setId(20L);
        active2.setIsActive(true);

        when(enrollmentService.getEnrollmentsForStudent(1L))
                .thenReturn(List.of(enrollment));
        when(courseService.getActiveCourses())
                .thenReturn(List.of(active1, active2));

        String viewName = controller.listAvailableCourses(model, principal);

        assertThat(viewName).isEqualTo("student/courses/list");
        verify(model).addAttribute(eq("courses"), argThat(list ->
                list instanceof List<?> l &&
                        l.size() == 1 &&
                        ((Course) l.get(0)).getId().equals(20L)
        ));
        verify(enrollmentService).getEnrollmentsForStudent(1L);
        verify(courseService).getActiveCourses();
    }

    @Test
    void enroll_redirectsWithSuccessParam() {
        String viewName = controller.enroll(5L, principal);

        assertThat(viewName).isEqualTo("redirect:/student/courses?success=enrolled");
        verify(enrollmentService).enrollStudent(1L, 5L);
    }

    @Test
    void dropCourse_redirectsToMyCourses() {
        String viewName = controller.dropCourse(7L, principal);

        assertThat(viewName).isEqualTo("redirect:/student/courses/my-courses?success=dropped");
        verify(enrollmentService).dropStudent(1L, 7L);
    }

    @Test
    void myCourses_addsOnlyActiveCourseEnrollments() {
        Course activeCourse = new Course();
        activeCourse.setId(1L);
        activeCourse.setIsActive(true);

        Course inactiveCourse = new Course();
        inactiveCourse.setId(2L);
        inactiveCourse.setIsActive(false);

        Enrollment activeEnrollment = new Enrollment();
        activeEnrollment.setCourse(activeCourse);

        Enrollment inactiveEnrollment = new Enrollment();
        inactiveEnrollment.setCourse(inactiveCourse);

        when(enrollmentService.getEnrollmentsForStudent(1L))
                .thenReturn(List.of(activeEnrollment, inactiveEnrollment));

        String viewName = controller.myCourses(model, principal);

        assertThat(viewName).isEqualTo("student/courses/my_enrollments");
        verify(model).addAttribute(eq("enrollments"), argThat(list ->
                list instanceof List<?> l &&
                        l.size() == 1 &&
                        ((Enrollment) l.get(0)).getCourse().getId().equals(1L)
        ));
    }

    @Test
    void viewGrades_setsCountsAndActiveEnrollments() {
        Course activeCourse1 = new Course();
        activeCourse1.setId(1L);
        activeCourse1.setIsActive(true);

        Course activeCourse2 = new Course();
        activeCourse2.setId(2L);
        activeCourse2.setIsActive(true);

        Course inactiveCourse = new Course();
        inactiveCourse.setId(3L);
        inactiveCourse.setIsActive(false);

        Enrollment graded = new Enrollment();
        graded.setCourse(activeCourse1);
        Grade grade5 = new Grade();
        grade5.setGradeValue(5);
        graded.setTempGrade(grade5);

        Enrollment pending = new Enrollment();
        pending.setCourse(activeCourse2);
        pending.setTempGrade(null);

        Enrollment inactive = new Enrollment();
        inactive.setCourse(inactiveCourse);
        Grade grade4 = new Grade();
        grade4.setGradeValue(4);
        inactive.setTempGrade(grade4);

        when(enrollmentService.getEnrollmentsForStudent(1L))
                .thenReturn(List.of(graded, pending, inactive));

        String viewName = controller.viewGrades(model, principal);

        assertThat(viewName).isEqualTo("student/grades/list");
        verify(model).addAttribute(eq("enrollments"), argThat(list ->
                list instanceof List<?> l && l.size() == 2
        ));
        verify(model).addAttribute("gradedCount", 1L);
        verify(model).addAttribute("pendingCount", 1L);
    }
}