package hr.algebra.uni_course_management.controller.api;

import hr.algebra.uni_course_management.exception.ResourceNotFoundException;
import hr.algebra.uni_course_management.model.Enrollment;
import hr.algebra.uni_course_management.model.User;
import hr.algebra.uni_course_management.service.EnrollmentService;
import hr.algebra.uni_course_management.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class EnrollmentRestController {
    private final EnrollmentService enrollmentService;
    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> getCurrentStudentEnrollments(Authentication authentication) {
        try {
            String username = authentication.getName();
            List<Enrollment> enrollments = enrollmentService.findByStudentUsername(username);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Enrollments retrieved successfully");
            response.put("data", enrollments);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Failed to retrieve enrollments: " + e.getMessage());
            error.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping("/{courseId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> enrollInCourse(@PathVariable Long courseId, Authentication authentication) {
        try {
            String username = authentication.getName();

            Optional<User> student = userService.findByUsername(username);
            Long studentId = student.map(User::getId).orElseThrow(() -> new ResourceNotFoundException("Student not found"));
            Enrollment enrollment = enrollmentService.enrollStudent(studentId, courseId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Successfully enrolled in course");
            response.put("data", enrollment);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (ResourceNotFoundException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            error.put("data", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (RuntimeException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            error.put("data", null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Failed to enroll in course: " + e.getMessage());
            error.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @DeleteMapping("/{courseId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> dropFromCourse(@PathVariable Long courseId, Authentication authentication) {
        try {
            String username = authentication.getName();
            Long studentId = userService.findByUsername(username)
                    .map(User::getId)
                    .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

            enrollmentService.dropStudent(studentId, courseId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Successfully dropped from course");
            response.put("data", null);

            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            error.put("data", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Failed to unenroll from course: " + e.getMessage());
            error.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}