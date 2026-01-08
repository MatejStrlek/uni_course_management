package hr.algebra.uni_course_management.controller.mvc;

import hr.algebra.uni_course_management.model.ContentType;
import hr.algebra.uni_course_management.model.Course;
import hr.algebra.uni_course_management.model.CourseContent;
import hr.algebra.uni_course_management.service.CourseContentService;
import hr.algebra.uni_course_management.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/courses")
public class AdminCourseContentController {
    private final CourseService courseService;
    private final CourseContentService courseContentService;

    @GetMapping("/{courseId}/content")
    public String viewCourseContent(@PathVariable Long courseId,
                                    Model model) {
        List<CourseContent> contents = courseContentService.getAllCourseContents(courseId);
        Course course = courseService.getCourseById(courseId);

        long publishedCount = contents.stream().filter(CourseContent::getIsPublished).count();
        long draftCount = contents.size() - publishedCount;

        model.addAttribute("contents", contents);
        model.addAttribute("course", course);
        model.addAttribute("contentTypes", ContentType.values());
        model.addAttribute("publishedCount", publishedCount);
        model.addAttribute("draftCount", draftCount);

        return "admin/content/list";
    }

    @GetMapping("/{courseId}/content/create")
    public String showCreateContentForm(@PathVariable Long courseId, Model model) {
        CourseContent courseContent = new CourseContent();
        Course course = courseService.getCourseById(courseId);

        model.addAttribute("courseContent", courseContent);
        model.addAttribute("course", course);
        model.addAttribute("contentTypes", ContentType.values());

        return "admin/content/create";
    }

    @PostMapping("/{courseId}/content/create")
    public String createContent(@PathVariable Long courseId,
                                @Valid @ModelAttribute("courseContent") CourseContent courseContent,
                                BindingResult result,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        if (result.hasErrors()) {
            Course course = courseService.getCourseById(courseId);
            model.addAttribute("course", course);
            model.addAttribute("contentTypes", ContentType.values());
            return "admin/content/create";
        }
        courseContentService.createContent(courseId, courseContent);

        redirectAttributes.addFlashAttribute("successMessage", "Content created successfully!");
        return "redirect:/admin/courses/" + courseId + "/content";
    }

    @GetMapping("/{courseId}/content/{contentId}/edit")
    public String showEditContentForm(@PathVariable Long courseId,
                                      @PathVariable Long contentId,
                                      Model model) {
        CourseContent courseContent = courseContentService.getContentById(contentId);
        Course course = courseService.getCourseById(courseId);

        model.addAttribute("courseContent", courseContent);
        model.addAttribute("course", course);
        model.addAttribute("contentTypes", ContentType.values());

        return "admin/content/edit";
    }

    @PostMapping("/{courseId}/content/{contentId}/update")
    public String updateContent(@PathVariable Long courseId,
                                @PathVariable Long contentId,
                                @Valid @ModelAttribute("courseContent") CourseContent courseContent,
                                BindingResult result,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        if (result.hasErrors()) {
            Course course = courseService.getCourseById(courseId);
            model.addAttribute("course", course);
            model.addAttribute("contentTypes", ContentType.values());

            return "admin/content/edit";
        }
        courseContentService.updateContent(contentId, courseContent);

        redirectAttributes.addFlashAttribute("successMessage", "Content updated successfully!");
        return "redirect:/admin/courses/" + courseId + "/content";
    }

    @PostMapping("/{courseId}/content/{contentId}/delete")
    public String deleteContent(@PathVariable Long courseId,
                                @PathVariable Long contentId,
                                RedirectAttributes redirectAttributes) {
        courseContentService.deleteContent(contentId);

        redirectAttributes.addFlashAttribute("successMessage", "Content deleted successfully!");
        return "redirect:/admin/courses/" + courseId + "/content";
    }

    @PostMapping("/{courseId}/content/{contentId}/toggle")
    public String togglePublishStatus(@PathVariable Long courseId,
                                      @PathVariable Long contentId,
                                      RedirectAttributes redirectAttributes) {
        CourseContent content = courseContentService.togglePublishStatus(contentId);
        String message = Boolean.TRUE.equals(content.getIsPublished())
                ? "Content published successfully!"
                : "Content unpublished successfully!";

        redirectAttributes.addFlashAttribute("successMessage", message);
        return "redirect:/admin/courses/" + courseId + "/content";
    }
}