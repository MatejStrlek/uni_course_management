package hr.algebra.uni_course_management.controller.api;

import hr.algebra.uni_course_management.exception.ResourceNotFoundException;
import hr.algebra.uni_course_management.model.ContentType;
import hr.algebra.uni_course_management.model.CourseContent;
import hr.algebra.uni_course_management.service.CourseContentService;
import hr.algebra.uni_course_management.service.CourseService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
@Validated
public class CourseContentRestController {
    private final CourseContentService courseContentService;
    private final CourseService courseService;

    @GetMapping("/{courseId}/content")
    @PreAuthorize("hasRole('ROLE_PROFESSOR') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getCourseContent(@PathVariable @NotNull Long courseId) {
        try {
            courseService.getCourseById(courseId);
            List<CourseContent> content = courseContentService.getAllCourseContents(courseId);

            if (content.isEmpty()) {
                throw new ResourceNotFoundException("No content found for course with id: " + courseId);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Course content retrieved successfully");
            response.put("data", content);

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
            error.put("message", "Failed to retrieve course content: " + e.getMessage());
            error.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping("/{courseId}/content")
    @PreAuthorize("hasRole('ROLE_PROFESSOR') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> createContent(
            @PathVariable @NotNull Long courseId,
            @RequestParam @NotNull String title,
            @RequestParam @NotNull String content,
            @RequestParam ContentType contentType) {
        try {
            courseService.getCourseById(courseId);
            CourseContent courseContent = new CourseContent();
            courseContent.setContentTitle(title);
            courseContent.setContent(content);
            courseContent.setContentType(contentType);
            courseContent.setIsPublished(false);

            var savedContent = courseContentService.createContent(courseId, courseContent);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Content created successfully");
            response.put("data", savedContent);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (ResourceNotFoundException | IllegalArgumentException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            error.put("data", null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Failed to create content: " + e.getMessage());
            error.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PutMapping("/{courseId}/content/{contentId}")
    @PreAuthorize("hasRole('ROLE_PROFESSOR') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> updateContent(
            @PathVariable @NotNull Long courseId,
            @PathVariable @NotNull Long contentId,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) ContentType contentType,
            @RequestParam(required = false) Boolean isPublished) {
        try {
            courseService.getCourseById(courseId);
            CourseContent courseContent = courseContentService.getContentById(contentId);

            if (title != null) courseContent.setContentTitle(title);
            if (content != null) courseContent.setContent(content);
            if (contentType != null) courseContent.setContentType(contentType);
            if (isPublished != null) courseContent.setIsPublished(isPublished);

            var updatedContent = courseContentService.updateContent(contentId, courseContent);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Content updated successfully");
            response.put("data", updatedContent);

            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException | IllegalArgumentException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            error.put("data", null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Failed to update content: " + e.getMessage());
            error.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @DeleteMapping("/{courseId}/content/{contentId}")
    @PreAuthorize("hasRole('ROLE_PROFESSOR') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteContent(
            @PathVariable @NotNull Long courseId,
            @PathVariable @NotNull Long contentId) {
        try {
            courseService.getCourseById(courseId);
            courseContentService.deleteContent(contentId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Content deleted successfully");
            response.put("data", null);

            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException | IllegalArgumentException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            error.put("data", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Failed to delete content: " + e.getMessage());
            error.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PutMapping("/{courseId}/content/{contentId}/publish")
    @PreAuthorize("hasRole('ROLE_PROFESSOR') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> togglePublish(
            @PathVariable @NotNull Long courseId,
            @PathVariable @NotNull Long contentId) {
        try {
            courseService.getCourseById(courseId);
            CourseContent content = courseContentService.togglePublishStatus(contentId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", Boolean.TRUE.equals(content.getIsPublished()) ?
                    "Content published successfully!" : "Content unpublished successfully!");
            response.put("data", content);

            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException | IllegalArgumentException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            error.put("data", null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Failed to toggle publish status: " + e.getMessage());
            error.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}