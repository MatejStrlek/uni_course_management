package hr.algebra.uni_course_management.service;

import hr.algebra.uni_course_management.model.Grade;
import hr.algebra.uni_course_management.repository.EnrollmentRepository;
import hr.algebra.uni_course_management.repository.GradeRepository;
import hr.algebra.uni_course_management.scheduler.EmailService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class GradeService {
    private final GradeRepository gradeRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final EmailService emailService;

    public void assignGrade(Long enrollmentId, Integer gradeValue) {
        var enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid enrollment ID: " + enrollmentId));

        if (gradeValue < 1 || gradeValue > 5) {
            throw new IllegalArgumentException("Grade value must be between 1 and 5.");
        }

        Grade grade = gradeRepository.findByEnrollmentId(enrollmentId)
                .orElseGet(() -> {
                    Grade newGrade = new Grade();
                    newGrade.setEnrollment(enrollment);
                    return newGrade;
                });

        grade.setGradeValue(gradeValue);
        grade.setGradedAt(LocalDateTime.now());

        gradeRepository.save(grade);
        emailService.sendGradeNotification(enrollment.getStudent(), grade);
    }

    public Grade getGradeForEnrollment(Long enrollmentId) {
        return gradeRepository.findByEnrollmentId(enrollmentId)
                .orElse(null);
    }
}