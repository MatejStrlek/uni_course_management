package hr.algebra.uni_course_management.service;

import hr.algebra.uni_course_management.model.Course;
import hr.algebra.uni_course_management.model.CourseContent;
import hr.algebra.uni_course_management.repository.CourseContentRepository;
import hr.algebra.uni_course_management.repository.CourseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseContentServiceTest {
    @Mock
    private CourseContentRepository courseContentRepository;

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private CourseContentService courseContentService;

    private Course course;
    private CourseContent content;

    @BeforeEach
    void setUp() {
        course = new Course();
        course.setId(1L);

        content = new CourseContent();
        content.setId(10L);
        content.setContentTitle("Title");
        content.setContentDescription("Desc");
        content.setContent("Body");
        content.setIsPublished(false);
        content.setCourse(course);
    }

    // ---------- getAllCourseContents / getPublishedCourseContents ----------

    @Test
    void getAllCourseContents_ReturnsList() {
        when(courseContentRepository.findByCourseId(1L)).thenReturn(List.of(content));

        List<CourseContent> result = courseContentService.getAllCourseContents(1L);

        assertThat(result).containsExactly(content);
        verify(courseContentRepository).findByCourseId(1L);
    }

    @Test
    void getPublishedCourseContents_ReturnsList() {
        when(courseContentRepository.findByCourseIdAndIsPublishedTrue(1L))
                .thenReturn(List.of(content));

        List<CourseContent> result = courseContentService.getPublishedCourseContents(1L);

        assertThat(result).containsExactly(content);
        verify(courseContentRepository).findByCourseIdAndIsPublishedTrue(1L);
    }

    // ---------- getContentById ----------

    @Test
    void getContentById_Existing_ReturnsContent() {
        when(courseContentRepository.findById(10L)).thenReturn(Optional.of(content));

        CourseContent result = courseContentService.getContentById(10L);

        assertThat(result).isEqualTo(content);
        verify(courseContentRepository).findById(10L);
    }

    @Test
    void getContentById_NotExisting_Throws() {
        when(courseContentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> courseContentService.getContentById(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Content not found with id: 99");
    }

    // ---------- createContent ----------

    @Test
    void createContent_PublishedWithoutDate_SetsPublishDateAndSaves() {
        CourseContent toCreate = new CourseContent();
        toCreate.setIsPublished(true);
        toCreate.setPublishDate(null);

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(courseContentRepository.save(any(CourseContent.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        CourseContent result = courseContentService.createContent(1L, toCreate);

        assertThat(result.getCourse()).isEqualTo(course);
        assertThat(result.getIsPublished()).isTrue();
        assertThat(result.getPublishDate()).isNotNull();
        verify(courseContentRepository).save(result);
    }

    @Test
    void createContent_UnpublishedOrAlreadyHasDate_DoesNotOverwriteDate() {
        CourseContent toCreate = new CourseContent();
        toCreate.setIsPublished(false);
        LocalDateTime existingDate = LocalDateTime.now().minusDays(1);
        toCreate.setPublishDate(existingDate);

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(courseContentRepository.save(any(CourseContent.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        CourseContent result = courseContentService.createContent(1L, toCreate);

        assertThat(result.getPublishDate()).isEqualTo(existingDate);
        verify(courseContentRepository).save(result);
    }

    @Test
    void createContent_CourseNotFound_Throws() {
        when(courseRepository.findById(1L)).thenReturn(Optional.empty());

        CourseContent toCreate = new CourseContent();

        assertThatThrownBy(() -> courseContentService.createContent(1L, toCreate))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Course not found with id: 1");
    }

    // ---------- updateContent ----------

    @Test
    void updateContent_UpdatesFieldsAndSaves() {
        CourseContent existing = new CourseContent();
        existing.setId(10L);
        existing.setIsPublished(false);

        CourseContent updated = new CourseContent();
        updated.setContentTitle("New title");
        updated.setContentDescription("New desc");
        updated.setContent("New body");
        updated.setContentType(existing.getContentType());
        updated.setDueDate(LocalDateTime.now().plusDays(1));
        updated.setIsPublished(false);

        when(courseContentRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(courseContentRepository.save(any(CourseContent.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        CourseContent result = courseContentService.updateContent(10L, updated);

        assertThat(result.getContentTitle()).isEqualTo("New title");
        assertThat(result.getContentDescription()).isEqualTo("New desc");
        assertThat(result.getContent()).isEqualTo("New body");
        assertThat(result.getDueDate()).isEqualTo(updated.getDueDate());
        assertThat(result.getIsPublished()).isFalse();
        verify(courseContentRepository).save(result);
    }

    @Test
    void updateContent_PublishingNow_SetsPublishDate() {
        CourseContent existing = new CourseContent();
        existing.setId(10L);
        existing.setIsPublished(false);
        existing.setPublishDate(null);

        CourseContent updated = new CourseContent();
        updated.setIsPublished(true);

        when(courseContentRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(courseContentRepository.save(any(CourseContent.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        CourseContent result = courseContentService.updateContent(10L, updated);

        assertThat(result.getIsPublished()).isTrue();
        assertThat(result.getPublishDate()).isNotNull();
    }

    @Test
    void updateContent_AlreadyPublished_KeepsExistingDate() {
        LocalDateTime oldDate = LocalDateTime.now().minusDays(1);
        CourseContent existing = new CourseContent();
        existing.setId(10L);
        existing.setIsPublished(true);
        existing.setPublishDate(oldDate);

        CourseContent updated = new CourseContent();
        updated.setIsPublished(true);

        when(courseContentRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(courseContentRepository.save(any(CourseContent.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        CourseContent result = courseContentService.updateContent(10L, updated);

        assertThat(result.getPublishDate()).isEqualTo(oldDate);
    }

    @Test
    void updateContent_NotFound_Throws() {
        when(courseContentRepository.findById(10L)).thenReturn(Optional.empty());

        CourseContent updated = new CourseContent();

        assertThatThrownBy(() -> courseContentService.updateContent(10L, updated))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Content not found with id: 10");
    }

    // ---------- deleteContent ----------

    @Test
    void deleteContent_Existing_Deletes() {
        when(courseContentRepository.findById(10L)).thenReturn(Optional.of(content));

        courseContentService.deleteContent(10L);

        verify(courseContentRepository).delete(content);
    }

    @Test
    void deleteContent_NotFound_Throws() {
        when(courseContentRepository.findById(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> courseContentService.deleteContent(10L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Content not found with id: 10");
    }

    // ---------- togglePublishStatus ----------

    @Test
    void togglePublishStatus_FromUnpublished_SetsTrueAndDateIfNull() {
        CourseContent existing = new CourseContent();
        existing.setId(10L);
        existing.setIsPublished(false);
        existing.setPublishDate(null);

        when(courseContentRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(courseContentRepository.save(any(CourseContent.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        CourseContent result = courseContentService.togglePublishStatus(10L);

        assertThat(result.getIsPublished()).isTrue();
        assertThat(result.getPublishDate()).isNotNull();
        verify(courseContentRepository).save(existing);
    }

    @Test
    void togglePublishStatus_FromPublishedToUnpublished_DoesNotChangeDate() {
        LocalDateTime oldDate = LocalDateTime.now().minusDays(1);
        CourseContent existing = new CourseContent();
        existing.setId(10L);
        existing.setIsPublished(true);
        existing.setPublishDate(oldDate);

        when(courseContentRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(courseContentRepository.save(any(CourseContent.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        CourseContent result = courseContentService.togglePublishStatus(10L);

        assertThat(result.getIsPublished()).isFalse();
        assertThat(result.getPublishDate()).isEqualTo(oldDate);
    }

    @Test
    void togglePublishStatus_NotFound_Throws() {
        when(courseContentRepository.findById(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> courseContentService.togglePublishStatus(10L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Content not found with id: 10");
    }
}