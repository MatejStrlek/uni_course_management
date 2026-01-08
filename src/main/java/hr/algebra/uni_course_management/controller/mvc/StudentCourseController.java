package hr.algebra.uni_course_management.controller.mvc;

import hr.algebra.uni_course_management.model.Course;
import hr.algebra.uni_course_management.model.Enrollment;
import hr.algebra.uni_course_management.model.EnrollmentStatus;
import hr.algebra.uni_course_management.model.User;
import hr.algebra.uni_course_management.service.CourseService;
import hr.algebra.uni_course_management.service.EnrollmentService;
import hr.algebra.uni_course_management.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/student/courses")
@RequiredArgsConstructor
public class StudentCourseController {
    private final CourseService courseService;
    private final EnrollmentService enrollmentService;
    private final UserService userService;

    @GetMapping
    public String listAvailableCourses(Model model, Principal principal) {
        User current = userService.getCurrentUser(principal.getName());
        List<Enrollment> studentEnrollments = enrollmentService.getEnrollmentsForStudent(current.getId());

        Set<Long> activeEnrollments = studentEnrollments.stream()
                .filter(e -> e.getStatus() == EnrollmentStatus.ENROLLED)
                .map(e -> e.getCourse().getId())
                .collect(Collectors.toSet());
        List<Course> available = courseService.getActiveCourses().stream()
                .filter(c -> !activeEnrollments.contains(c.getId()))
                .toList();

        model.addAttribute("courses", available);
        return "student/courses/list";
    }

    @PostMapping("/enroll/{courseId}")
    public String enroll(@PathVariable Long courseId, Principal principal) {
        User currentUser = userService.getCurrentUser(principal.getName());
        enrollmentService.enrollStudent(currentUser.getId(), courseId);
        return "redirect:/student/courses?success=enrolled";
    }

    @PostMapping("/drop/{courseId}")
    public String dropCourse(@PathVariable Long courseId, Principal principal) {
        User current = userService.getCurrentUser(principal.getName());
        enrollmentService.dropStudent(current.getId(), courseId);
        return "redirect:/student/courses/my-courses?success=dropped";
    }

    @GetMapping("/my-courses")
    public String myCourses(Model model, Principal principal) {
        User currentUser = userService.getCurrentUser(principal.getName());
        List<Enrollment> allEnrollments = enrollmentService.getEnrollmentsForStudent(currentUser.getId());

        List<Enrollment> activeEnrollments = allEnrollments.stream()
                .filter(e -> e.getCourse().getIsActive())
                .toList();

        model.addAttribute("enrollments", activeEnrollments);
        return "student/courses/my_enrollments";
    }

    @GetMapping("/grades")
    public String viewGrades(Model model, Principal principal) {
        User currentUser = userService.getCurrentUser(principal.getName());
        List<Enrollment> enrollments = enrollmentService.getEnrollmentsForStudent(currentUser.getId());

        List<Enrollment> activeEnrollments = enrollments.stream()
                .filter(e -> e.getCourse().getIsActive())
                .toList();

        long gradedCount = activeEnrollments.stream()
                .filter(e -> e.getTempGrade() != null)
                .count();
        long pendingCount = activeEnrollments.size() - gradedCount;

        model.addAttribute("enrollments", activeEnrollments);
        model.addAttribute("gradedCount", gradedCount);
        model.addAttribute("pendingCount", pendingCount);

        return "student/grades/list";
    }
}