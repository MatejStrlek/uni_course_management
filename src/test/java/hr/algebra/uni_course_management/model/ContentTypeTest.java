package hr.algebra.uni_course_management.model;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class ContentTypeTest {
    @Test
    void getDisplayName_ReturnsCorrectStrings() {
        assertThat(ContentType.LECTURE.getDisplayName()).isEqualTo("Lecture");
        assertThat(ContentType.ASSIGNMENT.getDisplayName()).isEqualTo("Assignment");
        assertThat(ContentType.QUIZ.getDisplayName()).isEqualTo("Quiz");
        assertThat(ContentType.READING_MATERIAL.getDisplayName()).isEqualTo("Reading Material");
        assertThat(ContentType.ANNOUNCEMENT.getDisplayName()).isEqualTo("Announcement");
        assertThat(ContentType.OTHER.getDisplayName()).isEqualTo("Other");
    }

    @Test
    void allEnumValues_HaveNonEmptyDisplayName() {
        for (ContentType type : ContentType.values()) {
            String display = type.getDisplayName();
            assertThat(display).isNotNull().isNotBlank();
        }
    }
}