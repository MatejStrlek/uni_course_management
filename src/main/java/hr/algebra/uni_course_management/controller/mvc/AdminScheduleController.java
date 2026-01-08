package hr.algebra.uni_course_management.controller.mvc;

import hr.algebra.uni_course_management.model.ScheduleEntry;
import hr.algebra.uni_course_management.service.CourseService;
import hr.algebra.uni_course_management.service.AdminScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.DayOfWeek;

@Controller
@RequestMapping("/admin/schedule")
@RequiredArgsConstructor
public class AdminScheduleController {
    private final AdminScheduleService adminScheduleService;
    private final CourseService courseService;

    @GetMapping
    public String listSchedule(Model model) {
        model.addAttribute("scheduleEntries", adminScheduleService.findAllScheduleEntriesSorted());
        return "admin/schedule/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("courses", courseService.getAllCourses());
        model.addAttribute("scheduleEntry", new ScheduleEntry());
        model.addAttribute("daysOfWeek", DayOfWeek.values());
        return "admin/schedule/create";
    }

    @PostMapping("/create")
    public String createScheduleEntry(
            @RequestParam Long courseId,
            @Valid @ModelAttribute("scheduleEntry") ScheduleEntry scheduleEntry,
            Model model,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("courses", courseService.getAllCourses());
            model.addAttribute("daysOfWeek", DayOfWeek.values());
            return "admin/schedule/create";
        }
        adminScheduleService.createScheduleEntry(courseId, scheduleEntry);
        redirectAttributes.addFlashAttribute("successMessage", "Schedule entry created successfully.");
        return "redirect:/admin/schedule";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        ScheduleEntry scheduleEntry = adminScheduleService.getScheduleEntryById(id);
        model.addAttribute("courses", courseService.getAllCourses());
        model.addAttribute("scheduleEntry", scheduleEntry);
        model.addAttribute("daysOfWeek", DayOfWeek.values());
        model.addAttribute("selectedCourseId", scheduleEntry.getCourse().getId());
        return "admin/schedule/edit";
    }

    @PostMapping("/edit/{id}")
    public String updateScheduleEntry(
            @PathVariable Long id,
            @RequestParam Long courseId,
            @Valid @ModelAttribute("scheduleEntry") ScheduleEntry scheduleEntry,
            Model model,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("courses", courseService.getAllCourses());
            model.addAttribute("daysOfWeek", DayOfWeek.values());
            model.addAttribute("selectedCourseId", courseId);
            return "admin/schedule/edit";
        }
        adminScheduleService.updateScheduleEntry(id, courseId, scheduleEntry);
        redirectAttributes.addFlashAttribute("successMessage", "Schedule entry updated successfully.");
        return "redirect:/admin/schedule";
    }

    @PostMapping("/delete/{id}")
    public String deleteScheduleEntry(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        adminScheduleService.deleteScheduleEntry(id);
        redirectAttributes.addFlashAttribute("successMessage", "Schedule entry deleted successfully.");
        return "redirect:/admin/schedule";
    }
}