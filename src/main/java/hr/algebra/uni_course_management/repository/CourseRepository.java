package hr.algebra.uni_course_management.repository;

import hr.algebra.uni_course_management.model.Course;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends CrudRepository<Course, String> {
    Optional<Course> findByCourseCode(String courseCode);
    List<Course> findByIsActiveTrue();
    List<Course> findByProfessorId(Long professorId);

    @Query("SELECT c FROM Course c WHERE c.isActive = true AND c.courseName LIKE %?1%")
    List<Course> searchByName(String keyword);

    boolean existsByCourseCode(String courseCode);
}