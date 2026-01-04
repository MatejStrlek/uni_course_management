package hr.algebra.uni_course_management.scheduler;

import hr.algebra.uni_course_management.model.User;
import hr.algebra.uni_course_management.model.UserRole;
import hr.algebra.uni_course_management.repository.ScheduleEntryRepository;
import hr.algebra.uni_course_management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SchedulerService {
    private final EmailService emailService;
    private final UserRepository userRepository;
    private final ScheduleEntryRepository scheduleEntryRepository;

    // Runs every day at 7:00 AM
    // @Scheduled(cron = "0 0 7 * * ?")
    // for testing purposes, it runs every 2 minutes
    // @Scheduled(cron = "0 */2 * * * ?")
    public void sendDailyScheduleNotifications() {
        log.info("Sending Daily schedule notifications to users");

        LocalDate today = LocalDate.now();
        DayOfWeek dayOfWeek = today.getDayOfWeek();
        List<User> students = userRepository.findByRole(UserRole.STUDENT);

        for (User student : students) {
            var scheduleEntries = scheduleEntryRepository.findByStudentAndDayOfWeek(student, dayOfWeek);
            emailService.sendDailySchedule(student, scheduleEntries);
        }

        log.info("Finished sending Daily schedule notifications to users");
    }
}