package hr.algebra.uni_course_management.service;

import hr.algebra.uni_course_management.model.ContentType;
import hr.algebra.uni_course_management.model.Course;
import hr.algebra.uni_course_management.model.CourseContent;
import hr.algebra.uni_course_management.repository.CourseContentRepository;
import hr.algebra.uni_course_management.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CourseContentService {
    private final CourseContentRepository courseContentRepository;
    private final CourseRepository courseRepository;

    public List<CourseContent> getAllCourseContents(Long courseId, Long professorId) {
        validateProfessorOwnership(courseId, professorId);
        return courseContentRepository.findByCourseId(courseId);
    }

    public List<CourseContent> getPublishedCourseContents(Long courseId) {
        return courseContentRepository.findByCourseIdAndIsPublishedTrue(courseId);
    }

    public List<CourseContent> getContentsByType(Long courseId, ContentType type, Long professorId) {
        validateProfessorOwnership(courseId, professorId);
        return courseContentRepository.findByCourseIdAndContentType(courseId, type);
    }

    public CourseContent getContentById(Long contentId, Long professorId) {
        CourseContent content = courseContentRepository.findById(contentId)
                .orElseThrow(() -> new RuntimeException("Content not found with id: " + contentId));

        validateProfessorOwnership(content.getCourse().getId(), professorId);
        return content;
    }

    public void createContent(Long courseId, CourseContent content, Long professorId) {
        validateProfessorOwnership(courseId, professorId);

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + courseId));

        content.setCourse(course);

        if (Boolean.TRUE.equals(content.getIsPublished()) && content.getPublishDate() == null) {
            content.setPublishDate(LocalDateTime.now());
        }

        courseContentRepository.save(content);
    }

    public void updateContent(Long contentId, CourseContent updatedContent, Long professorId) {
        CourseContent existingContent = courseContentRepository.findById(contentId)
                .orElseThrow(() -> new RuntimeException("Content not found with id: " + contentId));

        validateProfessorOwnership(existingContent.getCourse().getId(), professorId);

        existingContent.setContentTitle(updatedContent.getContentTitle());
        existingContent.setContentDescription(updatedContent.getContentDescription());
        existingContent.setContent(updatedContent.getContent());
        existingContent.setContentType(updatedContent.getContentType());
        existingContent.setDueDate(updatedContent.getDueDate());

        boolean wasPublished = Boolean.TRUE.equals(existingContent.getIsPublished());
        boolean willBePublished = Boolean.TRUE.equals(updatedContent.getIsPublished());

        if (!wasPublished && willBePublished) {
            existingContent.setPublishDate(LocalDateTime.now());
        }

        existingContent.setIsPublished(updatedContent.getIsPublished());
        courseContentRepository.save(existingContent);
    }

    public void deleteContent(Long contentId, Long professorId) {
        CourseContent content = courseContentRepository.findById(contentId)
                .orElseThrow(() -> new RuntimeException("Content not found with id: " + contentId));
        validateProfessorOwnership(content.getCourse().getId(), professorId);
        courseContentRepository.delete(content);
    }

    public CourseContent togglePublishStatus(Long contentId, Long professorId) {
        CourseContent content = courseContentRepository.findById(contentId)
                .orElseThrow(() -> new RuntimeException("Content not found with id: " + contentId));

        validateProfessorOwnership(content.getCourse().getId(), professorId);

        boolean newStatus = !Boolean.TRUE.equals(content.getIsPublished());
        content.setIsPublished(newStatus);

        if (newStatus && content.getPublishDate() == null) {
            content.setPublishDate(LocalDateTime.now());
        }

        return courseContentRepository.save(content);
    }

    public List<CourseContent> getUpcomingAssignments(Long courseId) {
        return courseContentRepository.findUpcomingAssignments(courseId, LocalDateTime.now());
    }

    public Long getContentCount(Long courseId) {
        return courseContentRepository.countByCourseId(courseId);
    }

    private void validateProfessorOwnership(Long courseId, Long professorId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + courseId));

        if (course.getProfessor() == null || !course.getProfessor().getId().equals(professorId)) {
            throw new RuntimeException("You are not authorized to access this course content");
        }
    }
}