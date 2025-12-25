package hr.algebra.uni_course_management.repository;

import hr.algebra.uni_course_management.model.Course;
import hr.algebra.uni_course_management.model.Enrollment;
import hr.algebra.uni_course_management.model.EnrollmentStatus;
import hr.algebra.uni_course_management.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends CrudRepository<Enrollment, Long> {
    List<Enrollment> findByStudent(User student);
    List<Enrollment> findByCourse(Course course);
    Optional<Enrollment> findByStudentAndCourse(User student, Course course);
    boolean existsByStudentAndCourse(User student, Course course);
    List<Enrollment> findByStudentAndStatus(User student, EnrollmentStatus status);
    boolean existsByStudentAndCourseAndStatus(User student, Course course, EnrollmentStatus status);
}