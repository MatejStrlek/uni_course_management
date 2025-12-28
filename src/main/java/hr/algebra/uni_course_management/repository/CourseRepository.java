package hr.algebra.uni_course_management.repository;

import hr.algebra.uni_course_management.model.Course;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends CrudRepository<Course, Long> {
    Optional<Course> findByCourseCode(String courseCode);

    List<Course> findByIsActiveTrue();

    List<Course> findByProfessorIdAndIsActiveTrue(Long professorId);

    @Query("SELECT c FROM Course c WHERE c.isActive = true AND c.courseName LIKE %?1%")
    List<Course> searchByName(String keyword);

    boolean existsByCourseCode(String courseCode);
}