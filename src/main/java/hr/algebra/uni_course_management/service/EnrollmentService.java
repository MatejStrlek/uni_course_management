package hr.algebra.uni_course_management.service;

import hr.algebra.uni_course_management.model.Course;
import hr.algebra.uni_course_management.model.Enrollment;
import hr.algebra.uni_course_management.model.EnrollmentStatus;
import hr.algebra.uni_course_management.model.User;
import hr.algebra.uni_course_management.repository.CourseRepository;
import hr.algebra.uni_course_management.repository.EnrollmentRepository;
import hr.algebra.uni_course_management.repository.GradeRepository;
import hr.algebra.uni_course_management.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final GradeRepository gradeRepository;

    private static final String STUDENT_NOT_FOUND = "Student not found";
    private static final String COURSE_NOT_FOUND = "Course not found";

    public void enrollStudent(Long studentId, Long courseId) {
        User student = this.userRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException(STUDENT_NOT_FOUND));
        Course course = this.courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException(COURSE_NOT_FOUND));

        if (Boolean.FALSE.equals(course.getIsActive())) {
            throw new IllegalStateException("Cannot enroll in inactive course");
        }

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
                .orElseThrow(() -> new IllegalArgumentException(STUDENT_NOT_FOUND));
        List<Enrollment> enrollments = enrollmentRepository.findByStudentAndStatus(student, EnrollmentStatus.ENROLLED);
        enrollments.forEach(enrollment -> gradeRepository.findByEnrollmentId(enrollment.getId())
                .ifPresent(enrollment::setTempGrade));

        return enrollments;
    }

    public List<Enrollment> getActiveEnrollmentsForCourse(Long courseId) {
        Course course = courseRepository.findById(courseId).orElseThrow();
        return enrollmentRepository
                .findByCourseAndStatus(course, EnrollmentStatus.ENROLLED)
                .stream()
                .filter(enrollment -> enrollment.getStudent().getIsActive())
                .toList();
    }

    public void dropStudent(Long studentId, Long courseId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException(STUDENT_NOT_FOUND));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException(COURSE_NOT_FOUND));

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