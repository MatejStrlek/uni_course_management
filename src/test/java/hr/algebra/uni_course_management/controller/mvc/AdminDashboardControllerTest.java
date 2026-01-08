package hr.algebra.uni_course_management.controller.mvc;

import hr.algebra.uni_course_management.service.AdminStatsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminDashboardControllerTest {
    @Mock
    private AdminStatsService adminStatsService;
    @Mock
    private Model model;
    @InjectMocks
    private AdminDashboardController controller;

    @Test
    void viewDashboard_addsAllStatsToModelAndReturnsView() {
        when(adminStatsService.getTotalUsers()).thenReturn(100);
        when(adminStatsService.getActiveUsers()).thenReturn(85);
        when(adminStatsService.getTotalCourses()).thenReturn(50);
        when(adminStatsService.getActiveCourses()).thenReturn(45);
        when(adminStatsService.getTotalStudents()).thenReturn(200);
        when(adminStatsService.getTotalProfessors()).thenReturn(15);
        when(adminStatsService.getTotalEnrollments()).thenReturn(1200);

        String viewName = controller.viewDashboard(model);
        assertThat(viewName).isEqualTo("admin/dashboard");

        verify(adminStatsService).getTotalUsers();
        verify(adminStatsService).getActiveUsers();
        verify(adminStatsService).getTotalCourses();
        verify(adminStatsService).getActiveCourses();
        verify(adminStatsService).getTotalStudents();
        verify(adminStatsService).getTotalProfessors();
        verify(adminStatsService).getTotalEnrollments();

        verify(model).addAttribute("totalUsers", 100);
        verify(model).addAttribute("activeUsers", 85);
        verify(model).addAttribute("totalCourses", 50);
        verify(model).addAttribute("activeCourses", 45);
        verify(model).addAttribute("totalStudents", 200);
        verify(model).addAttribute("totalProfessors", 15);
        verify(model).addAttribute("totalEnrollments", 1200);
    }
}