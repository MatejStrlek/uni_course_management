package hr.algebra.uni_course_management.controller;

import hr.algebra.uni_course_management.model.Course;
import hr.algebra.uni_course_management.model.Semester;
import hr.algebra.uni_course_management.repository.UserRepository;
import hr.algebra.uni_course_management.service.CourseService;
import hr.algebra.uni_course_management.service.GradeExportService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/courses")
@RequiredArgsConstructor
public class AdminCourseController {
    private final CourseService courseService;
    private final UserRepository userRepository;
    private final GradeExportService gradeExportService;

    private static final String ADMIN_COURSES_EDIT = "admin/courses/edit";
    private static final String ADMIN_COURSES_CREATE = "admin/courses/create";
    private static final String SEMESTERS = "semesters";
    private static final String PROFESSORS = "professors";
    private static final String PROFESSOR = "PROFESSOR";

    @GetMapping
    public String getCourses(Model model) {
        model.addAttribute("courses", courseService.getAllCourses());
        return "admin/courses/list";
    }

    @GetMapping("/create")
    public String createCourseForm(Model model) {
        model.addAttribute("course", new Course());
        model.addAttribute(SEMESTERS, Semester.values());
        model.addAttribute(PROFESSORS,
                userRepository
                        .findAll()
                        .stream()
                        .filter(user -> user.getRole().name().equals(PROFESSOR))
        );
        return ADMIN_COURSES_CREATE;
    }

    @PostMapping("/create")
    public String createCourse(@Valid @ModelAttribute Course course,
                               BindingResult result,
                               Model model) {
        if (result.hasErrors()) {
            model.addAttribute(SEMESTERS, Semester.values());
            model.addAttribute(PROFESSORS,
                    userRepository
                            .findAll()
                            .stream()
                            .filter(user -> user.getRole().name().equals(PROFESSOR))
            );
            return ADMIN_COURSES_CREATE;
        }

        try {
            courseService.createCourse(course);
            return "redirect:/admin/courses?success=created";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", "Error creating course: " + e.getMessage());
            model.addAttribute(SEMESTERS, Semester.values());
            model.addAttribute(PROFESSORS,
                    userRepository
                            .findAll()
                            .stream()
                            .filter(user ->
                                    user
                                            .getRole()
                                            .name()
                                            .equals(PROFESSOR))
                            .toList()
            );
            return ADMIN_COURSES_CREATE;
        }
    }

    @GetMapping("/edit/{id}")
    public String editCourseForm(@PathVariable Long id, Model model) {
        Course course = courseService.getCourseById(id);
        model.addAttribute("course", course);
        model.addAttribute(SEMESTERS, Semester.values());
        model.addAttribute(PROFESSORS,
                userRepository
                        .findAll()
                        .stream()
                        .filter(user -> user.getRole().name().equals(PROFESSOR))
        );
        return ADMIN_COURSES_EDIT;
    }

    @PostMapping("/edit/{id}")
    public String editCourse(@PathVariable Long id,
                             @Valid @ModelAttribute Course course,
                             BindingResult result,
                             Model model) {
        if (result.hasErrors()) {
            model.addAttribute(SEMESTERS, Semester.values());
            model.addAttribute(PROFESSORS,
                    userRepository
                            .findAll()
                            .stream()
                            .filter(user -> user.getRole().name().equals(PROFESSOR))
            );
            return ADMIN_COURSES_EDIT;
        }

        try {
            courseService.updateCourse(id, course);
            return "redirect:/admin/courses?success=updated";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", "Error updating course: " + e.getMessage());
            model.addAttribute(SEMESTERS, Semester.values());
            model.addAttribute(PROFESSORS,
                    userRepository
                            .findAll()
                            .stream()
                            .filter(user ->
                                    user
                                            .getRole()
                                            .name()
                                            .equals(PROFESSOR))
                            .toList()
            );
            return ADMIN_COURSES_EDIT;
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return "redirect:/admin/courses?success=deleted";
    }

    @GetMapping("/{courseId}/export-grades")
    public String exportCourseGrades(@PathVariable Long courseId, HttpServletResponse response) {
        String csvData = gradeExportService.exportGradesCsv(courseId);
        Course course = courseService.getCourseById(courseId);
        String courseCode = course.getCourseCode().replaceAll("\\s+", "_");
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"course_" + courseCode + "_grades.csv\"");
        try {
            response.getWriter().write(csvData);
            response.getWriter().flush();
        } catch (Exception e) {
            return "redirect:/admin/courses?error=export_failed";
        }
        return null;
    }
}