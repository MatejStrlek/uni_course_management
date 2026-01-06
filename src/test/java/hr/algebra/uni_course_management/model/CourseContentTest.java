package hr.algebra.uni_course_management.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class CourseContentTest {
    @Test
    void onCreate_SetsCreatedAt() {
        CourseContent content = new CourseContent();
        assertThat(content.getCreatedAt()).isNull();
        content.onCreate();
        assertThat(content.getCreatedAt()).isNotNull();
    }

    @Test
    void onCreate_WhenPublishedAndNoPublishDate_SetsPublishDate() {
        CourseContent content = new CourseContent();
        content.setIsPublished(true);
        content.setPublishDate(null);

        content.onCreate();
        assertThat(content.getPublishDate()).isNotNull();
    }

    @Test
    void onCreate_WhenNotPublished_DoesNotSetPublishDate() {
        CourseContent content = new CourseContent();
        content.setIsPublished(false);

        content.onCreate();
        assertThat(content.getPublishDate()).isNull();
    }

    @Test
    void onCreate_WhenPublishDateAlreadySet_DoesNotOverride() {
        CourseContent content = new CourseContent();
        LocalDateTime existing = LocalDateTime.now().minusDays(1);
        content.setIsPublished(true);
        content.setPublishDate(existing);

        content.onCreate();
        assertThat(content.getPublishDate()).isEqualTo(existing);
    }

    @Test
    void onUpdate_SetsUpdatedAt() {
        CourseContent content = new CourseContent();
        assertThat(content.getUpdatedAt()).isNull();
        content.onUpdate();
        assertThat(content.getUpdatedAt()).isNotNull();
    }
}