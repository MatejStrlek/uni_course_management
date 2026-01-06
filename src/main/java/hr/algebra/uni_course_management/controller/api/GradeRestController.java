package hr.algebra.uni_course_management.controller.api;

import hr.algebra.uni_course_management.exception.ResourceNotFoundException;
import hr.algebra.uni_course_management.model.Grade;
import hr.algebra.uni_course_management.service.GradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/grades")
@RequiredArgsConstructor
public class GradeRestController {
    private final GradeService gradeService;

    @PostMapping
    @PreAuthorize("hasRole('PROFESSOR') or hasRole('ADMINISTRATOR')")
    public ResponseEntity<?> assignGrade(@RequestParam Long enrollmentId, @RequestParam Integer gradeValue) {
        try {
            Grade grade = gradeService.assignGrade(enrollmentId, gradeValue);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Grade assigned successfully");
            response.put("data", grade);

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
            error.put("message", "Failed to assign grade: " + e.getMessage());
            error.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('PROFESSOR', 'STUDENT', 'ADMINISTRATOR')")
    public ResponseEntity<?> getGrades(@RequestParam(required = false) Long courseId,
                                       @RequestParam(required = false) Long studentId) {
        try {
            List<?> grades;
            if (courseId != null) {
                grades = gradeService.getGradesByCourse(courseId);
            } else if (studentId != null) {
                grades = gradeService.getGradesByStudent(studentId);
            } else {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "Please provide courseId or studentId parameter");
                error.put("data", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Grades retrieved successfully");
            response.put("data", grades);

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
            error.put("message", "Failed to retrieve grades: " + e.getMessage());
            error.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}