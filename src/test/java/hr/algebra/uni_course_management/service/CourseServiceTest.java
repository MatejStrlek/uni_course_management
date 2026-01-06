package hr.algebra.uni_course_management.service;

import hr.algebra.uni_course_management.model.Course;
import hr.algebra.uni_course_management.model.Semester;
import hr.algebra.uni_course_management.repository.CourseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {
    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private CourseService courseService;

    private Course course;

    @BeforeEach
    void setUp() {
        course = new Course();
        course.setId(1L);
        course.setCourseName("Algorithms");
        course.setCourseCode("ALG101");
        course.setDescription("Desc");
        course.setCredits(5);
        course.setMaxStudents(30);
        course.setSemester(Semester.WINTER);
        course.setAcademicYear("2024/2025");
        course.setIsActive(true);
    }

    // ---------- getAllCourses ----------

    @Test
    void getAllCourses_ReturnsList() {
        when(courseRepository.findAll()).thenReturn(List.of(course));

        List<Course> result = courseService.getAllCourses();

        assertThat(result).containsExactly(course);
        verify(courseRepository).findAll();
    }

    // ---------- getActiveCourses ----------

    @Test
    void getActiveCourses_DelegatesToRepository() {
        when(courseRepository.findByIsActiveTrue()).thenReturn(List.of(course));

        List<Course> result = courseService.getActiveCourses();

        assertThat(result).containsExactly(course);
        verify(courseRepository).findByIsActiveTrue();
    }

    // ---------- getCourseById ----------

    @Test
    void getCourseById_Existing_ReturnsCourse() {
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        Course result = courseService.getCourseById(1L);

        assertThat(result).isEqualTo(course);
        verify(courseRepository).findById(1L);
    }

    @Test
    void getCourseById_NotExisting_Throws() {
        when(courseRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> courseService.getCourseById(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid course ID: 99");
    }

    // ---------- getCoursesByProfessorId ----------

    @Test
    void getCoursesByProfessorId_ReturnsList() {
        when(courseRepository.findByProfessorIdAndIsActiveTrue(7L))
                .thenReturn(List.of(course));

        List<Course> result = courseService.getCoursesByProfessorId(7L);

        assertThat(result).containsExactly(course);
        verify(courseRepository).findByProfessorIdAndIsActiveTrue(7L);
    }

    // ---------- createCourse ----------

    @Test
    void createCourse_NewCode_SavesAndReturnsCourse() {
        when(courseRepository.existsByCourseCode("ALG101")).thenReturn(false);
        when(courseRepository.save(any(Course.class))).thenReturn(course);

        Course result = courseService.createCourse(course);

        assertThat(result).isEqualTo(course);
        verify(courseRepository).save(course);
    }

    @Test
    void createCourse_DuplicateCode_Throws() {
        when(courseRepository.existsByCourseCode("ALG101")).thenReturn(true);

        assertThatThrownBy(() -> courseService.createCourse(course))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Course with code ALG101 already exists.");
        verify(courseRepository, never()).save(any());
    }

    // ---------- updateCourse ----------

    @Test
    void updateCourse_Existing_UpdatesFieldsAndSaves() {
        Course updated = new Course();
        updated.setCourseName("Updated");
        updated.setDescription("New desc");
        updated.setCredits(6);
        updated.setMaxStudents(40);
        updated.setSemester(Semester.SUMMER);
        updated.setAcademicYear("2025/2026");
        updated.setIsActive(false);
        updated.setProfessor(course.getProfessor());

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(courseRepository.save(any(Course.class))).thenAnswer(inv -> inv.getArgument(0));

        Course result = courseService.updateCourse(1L, updated);

        assertThat(result.getCourseName()).isEqualTo("Updated");
        assertThat(result.getDescription()).isEqualTo("New desc");
        assertThat(result.getCredits()).isEqualTo(6);
        assertThat(result.getMaxStudents()).isEqualTo(40);
        assertThat(result.getSemester()).isEqualTo(Semester.SUMMER);
        assertThat(result.getAcademicYear()).isEqualTo("2025/2026");
        assertThat(result.getIsActive()).isFalse();
        verify(courseRepository).save(result);
    }

    @Test
    void updateCourse_NotExisting_Throws() {
        when(courseRepository.findById(1L)).thenReturn(Optional.empty());

        Course updated = new Course();

        assertThatThrownBy(() -> courseService.updateCourse(1L, updated))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid course ID: 1");
    }

    // ---------- deleteCourse ----------

    @Test
    void deleteCourse_Existing_DeletesEntity() {
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        courseService.deleteCourse(1L);

        verify(courseRepository).delete(course);
    }

    @Test
    void deleteCourse_NotExisting_Throws() {
        when(courseRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> courseService.deleteCourse(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid course ID: 1");
    }
}