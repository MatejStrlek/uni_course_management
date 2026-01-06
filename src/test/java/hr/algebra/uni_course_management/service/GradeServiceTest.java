package hr.algebra.uni_course_management.service;

import hr.algebra.uni_course_management.model.Enrollment;
import hr.algebra.uni_course_management.model.Grade;
import hr.algebra.uni_course_management.repository.EnrollmentRepository;
import hr.algebra.uni_course_management.repository.GradeRepository;
import hr.algebra.uni_course_management.scheduler.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GradeServiceTest {
    @Mock
    private GradeRepository gradeRepository;
    @Mock
    private EnrollmentRepository enrollmentRepository;
    @Mock
    private EmailService emailService;

    @InjectMocks
    private GradeService gradeService;

    private Enrollment enrollment;
    private Grade existingGrade;

    @BeforeEach
    void setUp() {
        enrollment = new Enrollment();
        enrollment.setId(10L);

        existingGrade = new Grade();
        existingGrade.setId(1L);
        existingGrade.setEnrollment(enrollment);
        existingGrade.setGradeValue(3);
        existingGrade.setGradedAt(LocalDateTime.now().minusDays(1));
    }

    // ---------- assignGrade ----------

    @Test
    void assignGrade_NewGrade_CreatesAndSavesAndSendsEmail() {
        when(enrollmentRepository.findById(10L)).thenReturn(Optional.of(enrollment));
        when(gradeRepository.findByEnrollmentId(10L)).thenReturn(Optional.empty());
        when(gradeRepository.save(any(Grade.class))).thenAnswer(inv -> inv.getArgument(0));

        Grade result = gradeService.assignGrade(10L, 4);

        assertThat(result.getGradeValue()).isEqualTo(4);
        assertThat(result.getEnrollment()).isEqualTo(enrollment);
        assertThat(result.getGradedAt()).isNotNull();

        verify(gradeRepository).save(any(Grade.class));
        verify(emailService).sendGradeNotification(enrollment.getStudent(), result);
    }

    @Test
    void assignGrade_ExistingGrade_UpdatesValueAndTimestamp() {
        when(enrollmentRepository.findById(10L)).thenReturn(Optional.of(enrollment));
        when(gradeRepository.findByEnrollmentId(10L)).thenReturn(Optional.of(existingGrade));
        when(gradeRepository.save(any(Grade.class))).thenAnswer(inv -> inv.getArgument(0));

        LocalDateTime beforeCall = existingGrade.getGradedAt();

        Grade result = gradeService.assignGrade(10L, 5);

        assertThat(result).isSameAs(existingGrade);
        assertThat(result.getGradeValue()).isEqualTo(5);
        assertThat(result.getGradedAt()).isAfter(beforeCall);
        verify(emailService).sendGradeNotification(enrollment.getStudent(), result);
    }

    @Test
    void assignGrade_InvalidEnrollment_ThrowsException() {
        when(enrollmentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> gradeService.assignGrade(99L, 3))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid enrollment ID: 99");

        verifyNoInteractions(gradeRepository, emailService);
    }

    @Test
    void assignGrade_GradeBelowRange_ThrowsException() {
        when(enrollmentRepository.findById(10L)).thenReturn(Optional.of(enrollment));

        assertThatThrownBy(() -> gradeService.assignGrade(10L, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Grade value must be between 1 and 5.");

        verifyNoInteractions(gradeRepository, emailService);
    }

    @Test
    void assignGrade_GradeAboveRange_ThrowsException() {
        when(enrollmentRepository.findById(10L)).thenReturn(Optional.of(enrollment));

        assertThatThrownBy(() -> gradeService.assignGrade(10L, 6))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Grade value must be between 1 and 5.");

        verifyNoInteractions(gradeRepository, emailService);
    }

    // ---------- getGradeForEnrollment ----------

    @Test
    void getGradeForEnrollment_Existing_ReturnsGrade() {
        when(gradeRepository.findByEnrollmentId(10L)).thenReturn(Optional.of(existingGrade));

        Grade result = gradeService.getGradeForEnrollment(10L);

        assertThat(result).isEqualTo(existingGrade);
    }

    @Test
    void getGradeForEnrollment_NotExisting_ReturnsNull() {
        when(gradeRepository.findByEnrollmentId(10L)).thenReturn(Optional.empty());

        Grade result = gradeService.getGradeForEnrollment(10L);

        assertThat(result).isNull();
    }

    // ---------- getGradesByCourse ----------

    @Test
    void getGradesByCourse_FiltersByEnrollments() {
        Enrollment e1 = new Enrollment();
        e1.setId(1L);
        Enrollment e2 = new Enrollment();
        e2.setId(2L);
        when(enrollmentRepository.findByCourseId(100L)).thenReturn(List.of(e1, e2));

        Grade g1 = new Grade(); g1.setEnrollment(e1);
        Grade g2 = new Grade(); g2.setEnrollment(e2);
        Enrollment otherEnrollment = new Enrollment(); otherEnrollment.setId(99L);
        Grade g3 = new Grade(); g3.setEnrollment(otherEnrollment);
        when(gradeRepository.findAll()).thenReturn(List.of(g1, g2, g3));

        List<Grade> result = gradeService.getGradesByCourse(100L);

        assertThat(result).containsExactlyInAnyOrder(g1, g2);
    }

    // ---------- getGradesByStudent ----------

    @Test
    void getGradesByStudent_FiltersByEnrollments() {
        Enrollment e1 = new Enrollment();
        e1.setId(5L);
        when(enrollmentRepository.findByStudentId(7L)).thenReturn(List.of(e1));

        Grade g1 = new Grade(); g1.setEnrollment(e1);
        Enrollment otherEnrollment = new Enrollment(); otherEnrollment.setId(8L);
        Grade g2 = new Grade(); g2.setEnrollment(otherEnrollment);
        when(gradeRepository.findAll()).thenReturn(List.of(g1, g2));

        List<Grade> result = gradeService.getGradesByStudent(7L);

        assertThat(result).containsExactly(g1);
    }
}