package hr.algebra.uni_course_management.controller;

import hr.algebra.uni_course_management.model.Course;
import hr.algebra.uni_course_management.model.CourseContent;
import hr.algebra.uni_course_management.service.CourseContentService;
import hr.algebra.uni_course_management.service.CourseService;
import hr.algebra.uni_course_management.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/student/courses")
public class StudentCourseContentController {
    private final CourseService courseService;
    private final CourseContentService courseContentService;

    @GetMapping("/{courseId}/content")
    public String listContent(@PathVariable Long courseId,
                              Model model) {
        Course course = courseService.getCourseById(courseId);
        List<CourseContent> contents = courseContentService.getPublishedCourseContents(courseId);

        model.addAttribute("course", course);
        model.addAttribute("contents", contents);

        return "student/content/list";
    }

    @GetMapping("/{courseId}/content/{contentId}")
    public String viewContent(@PathVariable Long courseId,
                              @PathVariable Long contentId,
                              Model model) {
        Course course = courseService.getCourseById(courseId);
        CourseContent content = courseContentService.getContentById(contentId);

        if (content == null || !content.getIsPublished()) {
            model.addAttribute("errorMessage", "Content not found or not published.");
            model.addAttribute("course", course);
            return "student/content/list";
        }

        model.addAttribute("course", course);
        model.addAttribute("content", content);
        return "student/content/view";
    }
}