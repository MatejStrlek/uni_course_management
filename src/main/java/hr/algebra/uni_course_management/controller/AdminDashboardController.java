package hr.algebra.uni_course_management.controller;

import hr.algebra.uni_course_management.service.AdminStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {
    private final AdminStatsService adminStatsService;

    @GetMapping
    public String viewDashboard(Model model) {
        model.addAttribute("totalUsers", adminStatsService.getTotalUsers());
        model.addAttribute("activeUsers", adminStatsService.getActiveUsers());
        model.addAttribute("totalCourses", adminStatsService.getTotalCourses());
        model.addAttribute("activeCourses", adminStatsService.getActiveCourses());
        model.addAttribute("totalStudents", adminStatsService.getTotalStudents());
        model.addAttribute("totalProfessors", adminStatsService.getTotalProfessors());
        model.addAttribute("totalEnrollments", adminStatsService.getTotalEnrollments());
        return "admin/dashboard";
    }
}