package hr.algebra.uni_course_management.controller;

import hr.algebra.uni_course_management.model.*;
import hr.algebra.uni_course_management.service.*;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/professor/courses")
@RequiredArgsConstructor
public class ProfessorCourseController {
    private final UserService userService;
    private final CourseService courseService;
    private final EnrollmentService enrollmentService;
    private final GradeService gradeService;
    private final GradeExportService gradeExportService;
    private final CourseContentService courseContentService;

    @GetMapping
    public String professorCourses(Model model, Principal principal) {
        User professor = userService.getCurrentUser(principal.getName());
        List<Course> courses = courseService.getCoursesByProfessorId(professor.getId());

        for (Course course : courses) {
            course.setEnrolledStudents(enrollmentService.getActiveEnrollmentsForCourse(course.getId()).size());
        }

        model.addAttribute("courses", courses);
        return "professor/courses/list";
    }

    @GetMapping("/{courseId}/students")
    public String viewCourseStudents(@PathVariable Long courseId, Model model, Principal principal) {
        User professor = userService.getCurrentUser(principal.getName());
        Course course = courseService.getCourseById(courseId);

        if (!course.getProfessor().getId().equals(professor.getId())) {
            return "redirect:/professor/courses?error=unauthorized";
        }

        List<Enrollment> activeEnrollments = enrollmentService.getActiveEnrollmentsForCourse(courseId);
        List<Enrollment> enrollmentsWithGrades = activeEnrollments
                .stream()
                .map(enr -> {
                    enr.setTempGrade(gradeService.getGradeForEnrollment(enr.getId()));
                    return enr;
                })
                .toList();

        model.addAttribute("course", course);
        model.addAttribute("enrollments", enrollmentsWithGrades);
        return "professor/courses/students";
    }

    @PostMapping("/{courseId}/grade/{enrollmentId}")
    public String assignGrade(
            @PathVariable Long courseId,
            @PathVariable Long enrollmentId,
            @RequestParam Integer gradeValue,
            RedirectAttributes redirectAttributes
    ) {
        gradeService.assignGrade(enrollmentId, gradeValue);
        redirectAttributes.addFlashAttribute("success", "Grade assigned successfully.");
        return "redirect:/professor/courses/" + courseId + "/students";
    }

    @GetMapping("/{courseId}/export-grades")
    public void exportCourseGrades(@PathVariable Long courseId, HttpServletResponse response, Principal principal) {
        User professor = userService.getCurrentUser(principal.getName());
        Course course = courseService.getCourseById(courseId);

        if (!course.getProfessor().getId().equals(professor.getId())) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String csvData = gradeExportService.exportGradesCsv(courseId);
        String courseCode = course.getCourseCode().replaceAll("\\s+", "_");
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"course_" + courseCode + "_grades.csv\"");
        try {
            response.getWriter().write(csvData);
            response.getWriter().flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/{courseId}/content")
    public String viewCourseContent(@PathVariable Long courseId,
                                    Model model,
                                    Principal principal) {
        User professor = userService.findByUsername(principal.getName());
        List<CourseContent> contents = courseContentService.getAllCourseContents(courseId, professor.getId());
        Course course = courseService.getCourseById(courseId);

        model.addAttribute("contents", contents);
        model.addAttribute("course", course);
        model.addAttribute("contentTypes", ContentType.values());
        return "professor/content/list";
    }

    @GetMapping("/{courseId}/content/create")
    public String showCreateContentForm(@PathVariable Long courseId, Model model) {
        CourseContent courseContent = new CourseContent();
        Course course = courseService.getCourseById(courseId);

        model.addAttribute("courseContent", courseContent);
        model.addAttribute("course", course);
        model.addAttribute("contentTypes", ContentType.values());
        model.addAttribute("isEdit", false);

        return "professor/content/create";
    }

    @PostMapping("/{courseId}/content/create")
    public String createContent(@PathVariable Long courseId,
                                @Valid @ModelAttribute("courseContent") CourseContent courseContent,
                                BindingResult result,
                                Principal principal,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        if (result.hasErrors()) {
            Course course = courseService.getCourseById(courseId);
            model.addAttribute("course", course);
            model.addAttribute("contentTypes", ContentType.values());
            model.addAttribute("isEdit", false);
            return "professor/content/create";
        }
        User professor = userService.findByUsername(principal.getName());
        courseContentService.createContent(courseId, courseContent, professor.getId());

        redirectAttributes.addFlashAttribute("successMessage", "Content created successfully!");
        return "redirect:/professor/courses/" + courseId + "/content";
    }

    @GetMapping("/{courseId}/content/{contentId}/edit")
    public String showEditContentForm(@PathVariable Long courseId,
                                      @PathVariable Long contentId,
                                      Model model,
                                      Principal principal) {
        User professor = userService.findByUsername(principal.getName());
        CourseContent courseContent = courseContentService.getContentById(contentId, professor.getId());
        Course course = courseService.getCourseById(courseId);

        model.addAttribute("courseContent", courseContent);
        model.addAttribute("course", course);
        model.addAttribute("contentTypes", ContentType.values());
        model.addAttribute("isEdit", true);
        return "professor/content/edit";
    }

    @PostMapping("/{courseId}/content/{contentId}/update")
    public String updateContent(@PathVariable Long courseId,
                                @PathVariable Long contentId,
                                @Valid @ModelAttribute("courseContent") CourseContent courseContent,
                                BindingResult result,
                                Principal principal,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        if (result.hasErrors()) {
            Course course = courseService.getCourseById(courseId);
            model.addAttribute("course", course);
            model.addAttribute("contentTypes", ContentType.values());
            model.addAttribute("isEdit", true);
            return "professor/content/edit";
        }
        User professor = userService.findByUsername(principal.getName());
        courseContentService.updateContent(contentId, courseContent, professor.getId());

        redirectAttributes.addFlashAttribute("successMessage", "Content updated successfully!");
        return "redirect:/professor/courses/" + courseId + "/content";
    }

    @PostMapping("/{courseId}/content/{contentId}/delete")
    public String deleteContent(@PathVariable Long courseId,
                                @PathVariable Long contentId,
                                Principal principal,
                                RedirectAttributes redirectAttributes) {
        User professor = userService.findByUsername(principal.getName());
        courseContentService.deleteContent(contentId, professor.getId());

        redirectAttributes.addFlashAttribute("successMessage", "Content deleted successfully!");
        return "redirect:/professor/courses/" + courseId + "/content";
    }

    @PostMapping("/{courseId}/content/{contentId}/toggle-publish")
    public String togglePublishStatus(@PathVariable Long courseId,
                                      @PathVariable Long contentId,
                                      Principal principal,
                                      RedirectAttributes redirectAttributes) {
        User professor = userService.findByUsername(principal.getName());
        CourseContent content = courseContentService.togglePublishStatus(contentId, professor.getId());
        String message = Boolean.TRUE.equals(content.getIsPublished())
                ? "Content published successfully!"
                : "Content unpublished successfully!";

        redirectAttributes.addFlashAttribute("successMessage", message);
        return "redirect:/professor/courses/" + courseId + "/content";
    }
}