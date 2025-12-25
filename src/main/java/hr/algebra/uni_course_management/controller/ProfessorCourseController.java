package hr.algebra.uni_course_management.controller;

import hr.algebra.uni_course_management.model.Course;
import hr.algebra.uni_course_management.model.Enrollment;
import hr.algebra.uni_course_management.model.User;
import hr.algebra.uni_course_management.service.CourseService;
import hr.algebra.uni_course_management.service.EnrollmentService;
import hr.algebra.uni_course_management.service.GradeService;
import hr.algebra.uni_course_management.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/professor/courses")
@RequiredArgsConstructor
public class ProfessorCourseController {
    private final UserService userService;
    private final CourseService courseService;
    private final EnrollmentService enrollmentService;
    private final GradeService gradeService;

    @GetMapping
    public String professorCourses(Model model, Principal principal) {
        User professor = userService.getCurrentUser(principal.getName());
        List<Course> courses = courseService.getCoursesByProfessorId(professor.getId());

        for (Course course : courses) {
            course.setEnrolledStudents(enrollmentService.getActiveEnrollmentsForCourse(course.getId()).size());
        }

        model.addAttribute("courses", courses);
        return "professor/courses/list";
    }

    @GetMapping("/{courseId}/students")
    public String viewCourseStudents(@PathVariable Long courseId, Model model, Principal principal) {
        User professor = userService.getCurrentUser(principal.getName());
        Course course = courseService.getCourseById(courseId);

        if (!course.getProfessor().getId().equals(professor.getId())) {
            return "redirect:/professor/courses?error=unauthorized";
        }

        List<Enrollment> activeEnrollments = enrollmentService.getActiveEnrollmentsForCourse(courseId);
        List<Enrollment> enrollmentsWithGrades = activeEnrollments
                .stream()
                .map(enr -> {
                    enr.setTempGrade(gradeService.getGradeForEnrollment(enr.getId()));
                    return enr;
                })
                .toList();

        model.addAttribute("course", course);
        model.addAttribute("enrollments", enrollmentsWithGrades);
        return "professor/courses/students";
    }

    @PostMapping("/{courseId}/grade/{enrollmentId}")
    public String assignGrade(
            @PathVariable Long courseId,
            @PathVariable Long enrollmentId,
            @RequestParam Integer gradeValue,
            RedirectAttributes redirectAttributes
    ) {
        gradeService.assignGrade(enrollmentId, gradeValue);
        redirectAttributes.addFlashAttribute("success", "Grade assigned successfully.");
        return "redirect:/professor/courses/" + courseId + "/students";
    }
}