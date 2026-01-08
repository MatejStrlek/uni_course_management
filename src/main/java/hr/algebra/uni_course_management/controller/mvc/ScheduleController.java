package hr.algebra.uni_course_management.controller.mvc;

import hr.algebra.uni_course_management.model.User;
import hr.algebra.uni_course_management.service.ScheduleViewService;
import hr.algebra.uni_course_management.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ScheduleController {
    private final ScheduleViewService scheduleViewService;
    private final UserService userService;

    @GetMapping("/professor/schedule")
    public String viewProfessorSchedule(Model model, Principal principal) {
        User professor = userService.getCurrentUser(principal.getName());
        model.addAttribute("scheduleRows", scheduleViewService.getProfessorSchedule(professor.getId()));
        model.addAttribute("pageTitle", "Professor Schedule");
        return "/professor/schedule/list";
    }

    @GetMapping("/student/schedule")
    public String viewStudentSchedule(Model model, Principal principal) {
        User student = userService.getCurrentUser(principal.getName());
        model.addAttribute("scheduleRows", scheduleViewService.getStudentSchedule(student.getId()));
        model.addAttribute("pageTitle", "Student Schedule");
        return "/student/schedule/list";
    }
}