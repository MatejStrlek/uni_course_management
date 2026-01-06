package hr.algebra.uni_course_management.scheduler;

import hr.algebra.uni_course_management.model.Course;
import hr.algebra.uni_course_management.model.Enrollment;
import hr.algebra.uni_course_management.model.Grade;
import hr.algebra.uni_course_management.model.ScheduleEntry;
import hr.algebra.uni_course_management.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {
    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    private User buildStudent() {
        User student = new User();
        student.setFirstName("John");
        student.setEmail("john@example.com");
        return student;
    }

    private Grade buildGrade() {
        User student = buildStudent();

        Course course = new Course();
        course.setCourseName("Algorithms");

        Enrollment enrollment = new Enrollment();
        enrollment.setCourse(course);
        enrollment.setStudent(student);

        Grade grade = new Grade();
        grade.setEnrollment(enrollment);
        grade.setGradeValue(5);
        grade.setGradedAt(LocalDateTime.of(2024, 1, 2, 14, 30));
        return grade;
    }

    @Test
    void sendGradeNotification_SendsMailWithExpectedContent() {
        User student = buildStudent();
        Grade grade = buildGrade();

        emailService.sendGradeNotification(student, grade);

        ArgumentCaptor<SimpleMailMessage> captor =
                ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());

        SimpleMailMessage msg = captor.getValue();
        assertThat(msg.getTo()).containsExactly("john@example.com");
        assertThat(msg.getSubject())
                .isEqualTo("New grade posted for Algorithms");

        String body = msg.getText();
        assertThat(body).contains("Dear John,");
        assertThat(body).contains("A new grade has been posted for your course Algorithms.");
        assertThat(body).contains("Grade: 5");

        String formattedDate = grade.getGradedAt()
                .format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
        assertThat(body).contains("Graded on: " + formattedDate);
        assertThat(body).contains("University Course Management System");
    }

    @Test
    void sendGradeNotification_WhenMailSenderThrows_IsCaught() {
        User student = buildStudent();
        Grade grade = buildGrade();
        doThrow(new RuntimeException("mail down"))
                .when(mailSender).send(any(SimpleMailMessage.class));

        emailService.sendGradeNotification(student, grade);
        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendDailySchedule_WithEntries_ListsSchedule() {
        User student = buildStudent();

        Course c1 = new Course(); c1.setCourseName("Algorithms");
        Course c2 = new Course(); c2.setCourseName("Databases");

        ScheduleEntry e1 = new ScheduleEntry();
        e1.setCourse(c1);
        e1.setStartTime(LocalTime.of(8, 0));
        e1.setEndTime(LocalTime.of(9, 0));
        e1.setRoom("A1");

        ScheduleEntry e2 = new ScheduleEntry();
        e2.setCourse(c2);
        e2.setStartTime(LocalTime.of(10, 0));
        e2.setEndTime(LocalTime.of(11, 30));
        e2.setRoom("B2");

        emailService.sendDailySchedule(student, List.of(e1, e2));

        ArgumentCaptor<SimpleMailMessage> captor =
                ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());

        SimpleMailMessage msg = captor.getValue();
        assertThat(msg.getTo()).containsExactly("john@example.com");
        assertThat(msg.getSubject()).isEqualTo("Your schedule for today");

        String body = msg.getText();
        assertThat(body).contains("Dear John,");
        assertThat(body).contains("Here is your schedule for today:");
        assertThat(body).contains("Algorithms - 08:00 to 09:00 at A1");
        assertThat(body).contains("Databases - 10:00 to 11:30 at B2");
        assertThat(body).contains("Best regards");
    }

    @Test
    void sendDailySchedule_NoEntries_SaysNoClasses() {
        User student = buildStudent();

        emailService.sendDailySchedule(student, List.of());

        ArgumentCaptor<SimpleMailMessage> captor =
                ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());

        SimpleMailMessage msg = captor.getValue();
        String body = msg.getText();
        assertThat(body).contains("You have no scheduled classes today.");
    }

    @Test
    void sendDailySchedule_WhenMailSenderThrows_IsCaught() {
        User student = buildStudent();
        doThrow(new RuntimeException("mail down"))
                .when(mailSender).send(any(SimpleMailMessage.class));

        emailService.sendDailySchedule(student, List.of());

        verify(mailSender).send(any(SimpleMailMessage.class));
    }
}