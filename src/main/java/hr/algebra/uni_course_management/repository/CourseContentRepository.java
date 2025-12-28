package hr.algebra.uni_course_management.repository;

import hr.algebra.uni_course_management.model.ContentType;
import hr.algebra.uni_course_management.model.CourseContent;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CourseContentRepository extends CrudRepository<CourseContent, Long> {
    List<CourseContent> findByCourseId(Long courseId);
    List<CourseContent> findByCourseIdAndIsPublishedTrue(Long courseId);

    List<CourseContent> findByCourseIdAndContentType(Long courseId, ContentType contentType);

    List<CourseContent> findByCourseIdAndContentTypeAndIsPublishedTrue(Long courseId, ContentType contentType);

    @Query("SELECT cc FROM CourseContent cc WHERE cc.course.id = :courseId " +
            "AND cc.contentType = 'ASSIGNMENT' AND cc.dueDate > :now " +
            "AND cc.isPublished = true ORDER BY cc.dueDate ASC")
    List<CourseContent> findUpcomingAssignments(
            @Param("courseId") Long courseId,
            @Param("now") LocalDateTime now);

    Long countByCourseId(Long courseId);

    @Query("SELECT COUNT(cc) FROM CourseContent cc WHERE cc.course.professor.id = :professorId")
    Long countByProfessorId(@Param("professorId") Long professorId);
}