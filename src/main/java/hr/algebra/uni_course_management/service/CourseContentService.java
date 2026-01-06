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

    public List<CourseContent> getAllCourseContents(Long courseId) {
        return courseContentRepository.findByCourseId(courseId);
    }

    public List<CourseContent> getPublishedCourseContents(Long courseId) {
        return courseContentRepository.findByCourseIdAndIsPublishedTrue(courseId);
    }

    public CourseContent getContentById(Long contentId) {
        return courseContentRepository.findById(contentId)
                .orElseThrow(() -> new RuntimeException("Content not found with id: " + contentId));
    }

    public CourseContent createContent(Long courseId, CourseContent content) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + courseId));
        content.setCourse(course);

        if (Boolean.TRUE.equals(content.getIsPublished()) && content.getPublishDate() == null) {
            content.setPublishDate(LocalDateTime.now());
        }
        courseContentRepository.save(content);
        return content;
    }

    public CourseContent updateContent(Long contentId, CourseContent updatedContent) {
        CourseContent existingContent = courseContentRepository.findById(contentId)
                .orElseThrow(() -> new RuntimeException("Content not found with id: " + contentId));

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
        return existingContent;
    }

    public void deleteContent(Long contentId) {
        CourseContent content = courseContentRepository.findById(contentId)
                .orElseThrow(() -> new RuntimeException("Content not found with id: " + contentId));
        courseContentRepository.delete(content);
    }

    public CourseContent togglePublishStatus(Long contentId) {
        CourseContent content = courseContentRepository.findById(contentId)
                .orElseThrow(() -> new RuntimeException("Content not found with id: " + contentId));

        boolean newStatus = !Boolean.TRUE.equals(content.getIsPublished());
        content.setIsPublished(newStatus);

        if (newStatus && content.getPublishDate() == null) {
            content.setPublishDate(LocalDateTime.now());
        }

        return courseContentRepository.save(content);
    }
}