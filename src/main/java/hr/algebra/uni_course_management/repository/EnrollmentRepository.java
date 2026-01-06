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
    List<Enrollment> findByCourseAndStatus(Course course, EnrollmentStatus status);

    Optional<Enrollment> findByStudentAndCourse(User student, Course course);

    Optional<Enrollment> findByStudentAndCourseAndStatus(User student, Course course, EnrollmentStatus status);

    List<Enrollment> findByStudentAndStatus(User student, EnrollmentStatus status);

    List<Enrollment> findByCourseId(Long courseId);

    List<Enrollment> findByStudentId(Long studentId);
}