package hr.algebra.uni_course_management.scheduler;

import hr.algebra.uni_course_management.model.Grade;
import hr.algebra.uni_course_management.model.ScheduleEntry;
import hr.algebra.uni_course_management.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;

    @Async
    public void sendGradeNotification(User student, Grade grade) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(student.getEmail());
            message.setSubject("New grade posted for " + grade.getEnrollment().getCourse().getCourseName());
            message.setText("Dear " + student.getFirstName() + ",\n\n" +
                    "A new grade has been posted for your course " + grade.getEnrollment().getCourse().getCourseName() + ".\n" +
                    "Grade: " + grade.getGradeValue() + "\n\n" +
                    "Graded on: " + grade.getGradedAt().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")) + "\n\n" +
                    "Best regards,\n" +
                    "University Course Management System");

            mailSender.send(message);
            log.info("Grade notification email sent to {}", student.getEmail());
        } catch (Exception e) {
            log.error("Failed to send grade notification email to {}: {}", student.getEmail(), e.getMessage());
        }
    }

    @Async
    public void sendDailySchedule(User student, List<ScheduleEntry> scheduleEntries) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(student.getEmail());
            message.setSubject("Your schedule for today");

            StringBuilder scheduleText = new StringBuilder("Dear " + student.getFirstName() + ",\n\n" +
                    "Here is your schedule for today:\n\n");

            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

            if (scheduleEntries.isEmpty()) {
                scheduleText.append("You have no scheduled classes today.\n\n");
            } else {
                for (ScheduleEntry entry : scheduleEntries) {
                    scheduleText.append(entry.getCourse().getCourseName())
                            .append(" - ")
                            .append(entry.getStartTime().format(timeFormatter))
                            .append(" to ")
                            .append(entry.getEndTime().format(timeFormatter))
                            .append(" at ")
                            .append(entry.getRoom())
                            .append("\n");
                }
            }
            scheduleText.append("\nBest regards,\nUniversity Course Management System");

            message.setText(scheduleText.toString());

            mailSender.send(message);
            log.info("Daily schedule email sent to {}", student.getEmail());
        } catch (Exception e) {
            log.error("Failed to send daily schedule email to {}: {}", student.getEmail(), e.getMessage());
        }
    }
}