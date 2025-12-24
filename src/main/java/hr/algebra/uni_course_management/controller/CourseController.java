package hr.algebra.uni_course_management.controller;

import hr.algebra.uni_course_management.model.Course;
import hr.algebra.uni_course_management.model.Semester;
import hr.algebra.uni_course_management.repository.UserRepository;
import hr.algebra.uni_course_management.service.CourseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/courses")
public class CourseController {
    @Autowired
    private CourseService courseService;
    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public String getCourses(Model model) {
        model.addAttribute("courses", courseService.getAllCourses());
        return "admin/courses/list";
    }

    @GetMapping("/create")
    public String createCourseForm(Model model) {
        model.addAttribute("course", new Course());
        model.addAttribute("semesters", Semester.values());
        model.addAttribute("professors",
                userRepository
                .findAll()
                .stream()
                .filter(user -> user.getRole().name().equals("PROFESSOR"))
        );
        return "admin/courses/create";
    }

    @PostMapping("/create")
    public String createCourse(@Valid @ModelAttribute Course course,
                               BindingResult result,
                               Model model) {
        if (result.hasErrors()) {
            model.addAttribute("semesters", Semester.values());
            model.addAttribute("professors",
                    userRepository
                    .findAll()
                    .stream()
                    .filter(user -> user.getRole().name().equals("PROFESSOR"))
            );
            return "admin/courses/create";
        }

        try {
            courseService.createCourse(course);
            return "redirect:/admin/courses?success=created";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", "Error creating course: " + e.getMessage());
            model.addAttribute("semesters", Semester.values());
            model.addAttribute("professors",
                    userRepository
                    .findAll()
                    .stream()
                    .filter(user ->
                            user
                                    .getRole()
                                    .name()
                                    .equals("PROFESSOR"))
                            .toList()
            );
            return "admin/courses/create";
        }
    }

    @GetMapping("/edit/{id}")
    public String editCourseForm(@PathVariable Long id, Model model) {
        Course course = courseService.getCourseById(id);
        model.addAttribute("course", course);
        model.addAttribute("semesters", Semester.values());
        model.addAttribute("professors",
                userRepository
                .findAll()
                .stream()
                .filter(user -> user.getRole().name().equals("PROFESSOR"))
        );
        return "admin/courses/edit";
    }

    @PostMapping("/edit/{id}")
    public String editCourse(@PathVariable Long id,
                             @Valid @ModelAttribute Course course,
                             BindingResult result,
                             Model model) {
        if (result.hasErrors()) {
            model.addAttribute("semesters", Semester.values());
            model.addAttribute("professors",
                    userRepository
                    .findAll()
                    .stream()
                    .filter(user -> user.getRole().name().equals("PROFESSOR"))
            );
            return "admin/courses/edit";
        }

        try {
            courseService.updateCourse(id, course);
            return "redirect:/admin/courses?success=updated";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", "Error updating course: " + e.getMessage());
            model.addAttribute("semesters", Semester.values());
            model.addAttribute("professors",
                    userRepository
                    .findAll()
                    .stream()
                    .filter(user ->
                            user
                                    .getRole()
                                    .name()
                                    .equals("PROFESSOR"))
                            .toList()
            );
            return "admin/courses/edit";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return "redirect:/admin/courses?success=deleted";
    }
}