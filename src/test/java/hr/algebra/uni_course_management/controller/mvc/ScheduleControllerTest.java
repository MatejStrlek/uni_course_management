package hr.algebra.uni_course_management.controller.mvc;

import hr.algebra.uni_course_management.dto.ScheduleRow;
import hr.algebra.uni_course_management.model.User;
import hr.algebra.uni_course_management.service.ScheduleViewService;
import hr.algebra.uni_course_management.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.security.Principal;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScheduleControllerTest {
    @Mock
    private ScheduleViewService scheduleViewService;
    @Mock
    private UserService userService;
    @Mock
    private Model model;
    @Mock
    private Principal principal;
    @InjectMocks
    private ScheduleController controller;
    private User professor;
    private User student;

    @BeforeEach
    void setUp() {
        professor = new User();
        professor.setId(1L);
        professor.setEmail("prof@test.com");

        student = new User();
        student.setId(2L);
        student.setEmail("student@test.com");
    }

    @Test
    void viewProfessorSchedule_addsScheduleRowsAndTitleAndReturnsView() {
        when(principal.getName()).thenReturn(professor.getEmail());
        when(userService.getCurrentUser(professor.getEmail())).thenReturn(professor);

        ScheduleRow row1 = new ScheduleRow("MONDAY", null, null, "Course 1", "C1", "A101");
        ScheduleRow row2 = new ScheduleRow("TUESDAY", null, null, "Course 2", "C2", "B202");

        when(scheduleViewService.getProfessorSchedule(professor.getId()))
                .thenReturn(List.of(row1, row2));

        String viewName = controller.viewProfessorSchedule(model, principal);

        assertThat(viewName).isEqualTo("/professor/schedule/list");
        verify(userService).getCurrentUser(professor.getEmail());
        verify(scheduleViewService).getProfessorSchedule(professor.getId());
        verify(model).addAttribute("scheduleRows", List.of(row1, row2));
        verify(model).addAttribute("pageTitle", "Professor Schedule");
    }

    @Test
    void viewStudentSchedule_addsScheduleRowsAndTitleAndReturnsView() {
        when(principal.getName()).thenReturn(student.getEmail());
        when(userService.getCurrentUser(student.getEmail())).thenReturn(student);

        ScheduleRow rowA = new ScheduleRow("MONDAY", null, null, "Course A", "CA", "C101");
        ScheduleRow rowB = new ScheduleRow("WEDNESDAY", null, null, "Course B", "CB", "D202");

        when(scheduleViewService.getStudentSchedule(student.getId()))
                .thenReturn(List.of(rowA, rowB));

        String viewName = controller.viewStudentSchedule(model, principal);

        assertThat(viewName).isEqualTo("/student/schedule/list");
        verify(userService).getCurrentUser(student.getEmail());
        verify(scheduleViewService).getStudentSchedule(student.getId());
        verify(model).addAttribute("scheduleRows", List.of(rowA, rowB));
        verify(model).addAttribute("pageTitle", "Student Schedule");
    }
}