package hr.algebra.uni_course_management.controller;

import hr.algebra.uni_course_management.model.Course;
import hr.algebra.uni_course_management.model.Enrollment;
import hr.algebra.uni_course_management.model.EnrollmentStatus;
import hr.algebra.uni_course_management.model.User;
import hr.algebra.uni_course_management.service.CourseService;
import hr.algebra.uni_course_management.service.EnrollmentService;
import hr.algebra.uni_course_management.service.UserService;
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
public class StudentCourseController {
    private final CourseService courseService;
    private final EnrollmentService enrollmentService;
    private final UserService userService;

    public StudentCourseController(CourseService courseService,
                                   EnrollmentService enrollmentService,
                                   UserService userService) {
        this.courseService = courseService;
        this.enrollmentService = enrollmentService;
        this.userService = userService;
    }

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
        model.addAttribute("enrollments",
                enrollmentService.getEnrollmentsForStudent(currentUser.getId()));
        return "student/courses/my_courses";
    }
}