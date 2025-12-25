package hr.algebra.uni_course_management.service;

import hr.algebra.uni_course_management.model.Course;
import hr.algebra.uni_course_management.model.Enrollment;
import hr.algebra.uni_course_management.model.EnrollmentStatus;
import hr.algebra.uni_course_management.model.User;
import hr.algebra.uni_course_management.repository.CourseRepository;
import hr.algebra.uni_course_management.repository.EnrollmentRepository;
import hr.algebra.uni_course_management.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    public EnrollmentService(EnrollmentRepository enrollmentRepository,
                             CourseRepository courseRepository,
                             UserRepository userRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
    }

    public void enrollStudent(Long studentId, Long courseId) {
        User student = this.userRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));
        Course course = this.courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));

        Optional<Enrollment> existing = this.enrollmentRepository.findByStudentAndCourse(student, course);

        Enrollment enrollment;
        if (existing.isPresent()) {
            enrollment = existing.get();
            if (enrollment.getStatus() == EnrollmentStatus.ENROLLED) {
                throw new IllegalStateException("Student is already enrolled in this course");
            }
        } else {
            enrollment = new Enrollment();
            enrollment.setStudent(student);
            enrollment.setCourse(course);
        }
        enrollment.setStatus(EnrollmentStatus.ENROLLED);
        this.enrollmentRepository.save(enrollment);
    }

    public List<Enrollment> getEnrollmentsForStudent(Long studentId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));
        return enrollmentRepository.findByStudentAndStatus(student, EnrollmentStatus.ENROLLED);
    }

    public List<Enrollment> getEnrollmentsForCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));
        return enrollmentRepository.findByCourse(course);
    }

    public void dropStudent(Long studentId, Long courseId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));

        Optional<Enrollment> enrollment = enrollmentRepository
                .findByStudentAndCourseAndStatus(student, course, EnrollmentStatus.ENROLLED);

        if (enrollment.isPresent()) {
            enrollment.get().setStatus(EnrollmentStatus.DROPPED);
            enrollmentRepository.save(enrollment.get());
        } else {
            throw new IllegalStateException("No active enrollment found");
        }
    }
}