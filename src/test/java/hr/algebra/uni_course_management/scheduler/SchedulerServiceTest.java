package hr.algebra.uni_course_management.scheduler;

import hr.algebra.uni_course_management.model.ScheduleEntry;
import hr.algebra.uni_course_management.model.User;
import hr.algebra.uni_course_management.model.UserRole;
import hr.algebra.uni_course_management.repository.ScheduleEntryRepository;
import hr.algebra.uni_course_management.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SchedulerServiceTest {
    @Mock
    private EmailService emailService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ScheduleEntryRepository scheduleEntryRepository;

    @InjectMocks
    private SchedulerService schedulerService;

    private User student1;
    private User student2;

    @BeforeEach
    void setUp() {
        student1 = new User();
        student1.setId(1L);
        student1.setUsername("student1");
        student1.setRole(UserRole.STUDENT);

        student2 = new User();
        student2.setId(2L);
        student2.setUsername("student2");
        student2.setRole(UserRole.STUDENT);
    }

    @Test
    void sendDailyScheduleNotifications_SendsEmailToEachStudent() {
        when(userRepository.findByRole(UserRole.STUDENT)).thenReturn(List.of(student1, student2));

        DayOfWeek today = LocalDate.now().getDayOfWeek();

        List<ScheduleEntry> s1Entries = List.of(new ScheduleEntry());
        List<ScheduleEntry> s2Entries = List.of(new ScheduleEntry());

        when(scheduleEntryRepository.findByStudentAndDayOfWeek(student1, today))
                .thenReturn(s1Entries);
        when(scheduleEntryRepository.findByStudentAndDayOfWeek(student2, today))
                .thenReturn(s2Entries);

        schedulerService.sendDailyScheduleNotifications();

        verify(userRepository).findByRole(UserRole.STUDENT);
        verify(scheduleEntryRepository).findByStudentAndDayOfWeek(student1, today);
        verify(scheduleEntryRepository).findByStudentAndDayOfWeek(student2, today);

        verify(emailService).sendDailySchedule(student1, s1Entries);
        verify(emailService).sendDailySchedule(student2, s2Entries);
        verifyNoMoreInteractions(emailService);
    }

    @Test
    void sendDailyScheduleNotifications_NoStudents_NoEmailsSent() {
        when(userRepository.findByRole(UserRole.STUDENT)).thenReturn(List.of());

        schedulerService.sendDailyScheduleNotifications();

        verify(userRepository).findByRole(UserRole.STUDENT);
        verifyNoInteractions(scheduleEntryRepository);
        verifyNoInteractions(emailService);
    }
}