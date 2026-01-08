package hr.algebra.uni_course_management.service;

import hr.algebra.uni_course_management.model.*;
import hr.algebra.uni_course_management.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnrollmentServiceTest {
    @Mock
    private EnrollmentRepository enrollmentRepository;
    @Mock
    private CourseRepository courseRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private GradeRepository gradeRepository;

    @InjectMocks
    private EnrollmentService enrollmentService;

    private User student;
    private Course activeCourse;
    private Course inactiveCourse;

    @BeforeEach
    void setUp() {
        student = new User();
        student.setId(1L);
        student.setUsername("student");
        student.setIsActive(true);

        activeCourse = new Course();
        activeCourse.setId(10L);
        activeCourse.setIsActive(true);

        inactiveCourse = new Course();
        inactiveCourse.setId(11L);
        inactiveCourse.setIsActive(false);
    }

    // ---------- enrollStudent ----------

    @Test
    void enrollStudent_NewEnrollment_Succeeds() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(student));
        when(courseRepository.findById(10L)).thenReturn(Optional.of(activeCourse));
        when(enrollmentRepository.findByStudentAndCourse(student, activeCourse))
                .thenReturn(Optional.empty());
        when(enrollmentRepository.save(any(Enrollment.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Enrollment result = enrollmentService.enrollStudent(1L, 10L);

        assertThat(result.getStudent()).isEqualTo(student);
        assertThat(result.getCourse()).isEqualTo(activeCourse);
        assertThat(result.getStatus()).isEqualTo(EnrollmentStatus.ENROLLED);
        verify(enrollmentRepository).save(result);
    }

    @Test
    void enrollStudent_ExistingEnrollmentPreviouslyDropped_Reenrolls() {
        Enrollment existing = new Enrollment();
        existing.setStudent(student);
        existing.setCourse(activeCourse);
        existing.setStatus(EnrollmentStatus.DROPPED);

        when(userRepository.findById(1L)).thenReturn(Optional.of(student));
        when(courseRepository.findById(10L)).thenReturn(Optional.of(activeCourse));
        when(enrollmentRepository.findByStudentAndCourse(student, activeCourse))
                .thenReturn(Optional.of(existing));
        when(enrollmentRepository.save(existing)).thenReturn(existing);

        Enrollment result = enrollmentService.enrollStudent(1L, 10L);

        assertThat(result).isSameAs(existing);
        assertThat(result.getStatus()).isEqualTo(EnrollmentStatus.ENROLLED);
        verify(enrollmentRepository).save(existing);
    }

    @Test
    void enrollStudent_AlreadyEnrolled_Throws() {
        Enrollment existing = new Enrollment();
        existing.setStudent(student);
        existing.setCourse(activeCourse);
        existing.setStatus(EnrollmentStatus.ENROLLED);

        when(userRepository.findById(1L)).thenReturn(Optional.of(student));
        when(courseRepository.findById(10L)).thenReturn(Optional.of(activeCourse));
        when(enrollmentRepository.findByStudentAndCourse(student, activeCourse))
                .thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> enrollmentService.enrollStudent(1L, 10L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Student is already enrolled in this course");
    }

    @Test
    void enrollStudent_InactiveCourse_Throws() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(student));
        when(courseRepository.findById(11L)).thenReturn(Optional.of(inactiveCourse));

        assertThatThrownBy(() -> enrollmentService.enrollStudent(1L, 11L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Cannot enroll in inactive course");
    }

    @Test
    void enrollStudent_StudentNotFound_Throws() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> enrollmentService.enrollStudent(1L, 10L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Student not found");
    }

    @Test
    void enrollStudent_CourseNotFound_Throws() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(student));
        when(courseRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> enrollmentService.enrollStudent(1L, 99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Course not found");
    }

    // ---------- getEnrollmentsForStudent ----------

    @Test
    void getEnrollmentsForStudent_ReturnsWithTempGrades() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(student));

        Enrollment e1 = new Enrollment();
        e1.setId(100L);
        Enrollment e2 = new Enrollment();
        e2.setId(101L);

        when(enrollmentRepository.findByStudentAndStatusIn(student, List.of(EnrollmentStatus.ENROLLED, EnrollmentStatus.COMPLETED)))
                .thenReturn(List.of(e1, e2));

        Grade g1 = new Grade(); g1.setGradeValue(5);
        when(gradeRepository.findByEnrollmentId(100L)).thenReturn(Optional.of(g1));
        when(gradeRepository.findByEnrollmentId(101L)).thenReturn(Optional.empty());

        List<Enrollment> result = enrollmentService.getEnrollmentsForStudent(1L);

        assertThat(result).containsExactlyInAnyOrder(e1, e2);
        assertThat(e1.getTempGrade()).isEqualTo(g1);
        assertThat(e2.getTempGrade()).isNull();
    }

    @Test
    void getEnrollmentsForStudent_StudentNotFound_Throws() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> enrollmentService.getEnrollmentsForStudent(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Student not found");
    }

    // ---------- findByStudentUsername ----------

    @Test
    void findByStudentUsername_ReturnsWithTempGrades() {
        when(userRepository.findByUsername("student")).thenReturn(Optional.of(student));

        Enrollment e1 = new Enrollment();
        e1.setId(200L);
        when(enrollmentRepository.findByStudentAndStatusIn(student, List.of(EnrollmentStatus.ENROLLED, EnrollmentStatus.COMPLETED)))
                .thenReturn(List.of(e1));

        Grade g = new Grade(); g.setGradeValue(4);
        when(gradeRepository.findByEnrollmentId(200L)).thenReturn(Optional.of(g));

        List<Enrollment> result = enrollmentService.findByStudentUsername("student");

        assertThat(result).containsExactly(e1);
        assertThat(e1.getTempGrade()).isEqualTo(g);
    }

    @Test
    void findByStudentUsername_StudentNotFound_Throws() {
        when(userRepository.findByUsername("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> enrollmentService.findByStudentUsername("missing"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Student not found");
    }

    // ---------- getActiveEnrollmentsForCourse ----------

    @Test
    void getActiveEnrollmentsForCourse_FiltersInactiveStudents() {
        when(courseRepository.findById(10L)).thenReturn(Optional.of(activeCourse));

        Enrollment e1 = new Enrollment();
        e1.setCourse(activeCourse);
        User activeStudent = new User(); activeStudent.setIsActive(true);
        e1.setStudent(activeStudent);

        Enrollment e2 = new Enrollment();
        e2.setCourse(activeCourse);
        User inactiveStudent = new User(); inactiveStudent.setIsActive(false);
        e2.setStudent(inactiveStudent);

        when(enrollmentRepository.findByCourseAndStatus(activeCourse, EnrollmentStatus.ENROLLED))
                .thenReturn(List.of(e1, e2));

        List<Enrollment> result = enrollmentService.getActiveEnrollmentsForCourse(10L);

        assertThat(result).containsExactly(e1);
    }

    // ---------- dropStudent ----------

    @Test
    void dropStudent_ExistingEnrollment_SetsDroppedAndSaves() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(student));
        when(courseRepository.findById(10L)).thenReturn(Optional.of(activeCourse));

        Enrollment e = new Enrollment();
        e.setStudent(student);
        e.setCourse(activeCourse);
        e.setStatus(EnrollmentStatus.ENROLLED);

        when(enrollmentRepository.findByStudentAndCourseAndStatus(
                student, activeCourse, EnrollmentStatus.ENROLLED))
                .thenReturn(Optional.of(e));

        enrollmentService.dropStudent(1L, 10L);

        assertThat(e.getStatus()).isEqualTo(EnrollmentStatus.DROPPED);
        verify(enrollmentRepository).save(e);
    }

    @Test
    void dropStudent_NoActiveEnrollment_Throws() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(student));
        when(courseRepository.findById(10L)).thenReturn(Optional.of(activeCourse));
        when(enrollmentRepository.findByStudentAndCourseAndStatus(
                student, activeCourse, EnrollmentStatus.ENROLLED))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> enrollmentService.dropStudent(1L, 10L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("No active enrollment found");
    }

    @Test
    void dropStudent_StudentNotFound_Throws() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> enrollmentService.dropStudent(1L, 10L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Student not found");
    }

    @Test
    void dropStudent_CourseNotFound_Throws() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(student));
        when(courseRepository.findById(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> enrollmentService.dropStudent(1L, 10L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Course not found");
    }
}