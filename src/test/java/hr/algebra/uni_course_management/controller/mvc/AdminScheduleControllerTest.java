package hr.algebra.uni_course_management.controller.mvc;

import hr.algebra.uni_course_management.model.Course;
import hr.algebra.uni_course_management.model.ScheduleEntry;
import hr.algebra.uni_course_management.service.AdminScheduleService;
import hr.algebra.uni_course_management.service.CourseService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.DayOfWeek;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminScheduleControllerTest {
    @Mock
    private AdminScheduleService adminScheduleService;
    @Mock
    private CourseService courseService;
    @Mock
    private BindingResult bindingResult;
    @Mock
    private RedirectAttributes redirectAttributes;
    @Mock
    private Model model;
    @Mock
    private ObjectError mockError;

    @InjectMocks
    private AdminScheduleController controller;

    @Test
    void listSchedule_addsScheduleEntriesAndReturnsListView() {
        List<ScheduleEntry> entries = List.of(new ScheduleEntry(), new ScheduleEntry());
        when(adminScheduleService.findAllScheduleEntriesSorted()).thenReturn(entries);

        String view = controller.listSchedule(mock(Model.class));

        assertThat(view).isEqualTo("admin/schedule/list");
        verify(adminScheduleService).findAllScheduleEntriesSorted();
    }

    @Test
    void showCreateForm_addsCoursesNewEntryAndDays() {
        List<Course> courses = List.of(new Course(), new Course());
        when(courseService.getAllCourses()).thenReturn(courses);
        String view = controller.showCreateForm(model);

        assertThat(view).isEqualTo("admin/schedule/create");
        verify(courseService).getAllCourses();

        verify(model).addAttribute(eq("scheduleEntry"), any(ScheduleEntry.class));
        verify(model).addAttribute(eq("daysOfWeek"), eq(DayOfWeek.values()));
    }

    @Test
    void createScheduleEntry_validationFails_returnsCreateForm() {
        ScheduleEntry entry = new ScheduleEntry();
        when(courseService.getAllCourses()).thenReturn(List.of());

        when(bindingResult.hasErrors()).thenReturn(true);

        String view = controller.createScheduleEntry(1L, entry, mock(Model.class), bindingResult, redirectAttributes);

        assertThat(view).isEqualTo("admin/schedule/create");
        verify(courseService).getAllCourses();
        verify(redirectAttributes, never()).addFlashAttribute(anyString(), anyString());
        verify(adminScheduleService, never()).createScheduleEntry(anyLong(), any());
    }

    @Test
    void createScheduleEntry_success_createsAndRedirects() {
        ScheduleEntry entry = new ScheduleEntry();
        when(bindingResult.hasErrors()).thenReturn(false);

        String view = controller.createScheduleEntry(1L, entry, mock(Model.class), bindingResult, redirectAttributes);

        assertThat(view).isEqualTo("redirect:/admin/schedule");
        verify(adminScheduleService).createScheduleEntry(1L, entry);
        verify(redirectAttributes).addFlashAttribute("successMessage", "Schedule entry created successfully.");
    }

    @Test
    void showEditForm_addsEntryCoursesDaysAndSelectedCourse() {
        ScheduleEntry entry = new ScheduleEntry();
        Course course = new Course();
        course.setId(5L);
        entry.setCourse(course);

        when(adminScheduleService.getScheduleEntryById(10L)).thenReturn(entry);
        when(courseService.getAllCourses()).thenReturn(List.of());

        String view = controller.showEditForm(10L, model);

        assertThat(view).isEqualTo("admin/schedule/edit");
        verify(adminScheduleService).getScheduleEntryById(10L);
        verify(courseService).getAllCourses();
        verify(model).addAttribute("selectedCourseId", 5L);
    }

    @Test
    void updateScheduleEntry_validationFails_returnsEditForm() {
        ScheduleEntry entry = new ScheduleEntry();
        when(courseService.getAllCourses()).thenReturn(List.of());
        when(bindingResult.hasErrors()).thenReturn(true);

        String view = controller.updateScheduleEntry(10L, 1L, entry, mock(Model.class), bindingResult, redirectAttributes);

        assertThat(view).isEqualTo("admin/schedule/edit");
        verify(courseService).getAllCourses();
        verify(adminScheduleService, never()).updateScheduleEntry(anyLong(), anyLong(), any());
    }

    @Test
    void updateScheduleEntry_success_updatesAndRedirects() {
        ScheduleEntry entry = new ScheduleEntry();
        when(bindingResult.hasErrors()).thenReturn(false);

        String view = controller.updateScheduleEntry(10L, 1L, entry, mock(Model.class), bindingResult, redirectAttributes);

        assertThat(view).isEqualTo("redirect:/admin/schedule");
        verify(adminScheduleService).updateScheduleEntry(10L, 1L, entry);
        verify(redirectAttributes).addFlashAttribute("successMessage", "Schedule entry updated successfully.");
    }

    @Test
    void deleteScheduleEntry_callsServiceAndRedirects() {
        String view = controller.deleteScheduleEntry(10L, redirectAttributes);

        assertThat(view).isEqualTo("redirect:/admin/schedule");
        verify(adminScheduleService).deleteScheduleEntry(10L);
        verify(redirectAttributes).addFlashAttribute("successMessage", "Schedule entry deleted successfully.");
    }
}