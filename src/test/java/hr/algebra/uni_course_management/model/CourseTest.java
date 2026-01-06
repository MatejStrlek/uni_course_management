package hr.algebra.uni_course_management.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class CourseTest {
    @Test
    void prePersist_SetsCreatedAtOnlyOnce() {
        Course course = new Course("ALG101", "Algorithms", "Desc", 5);
        assertThat(course.getCreatedAt()).isNull();
        course.onCreate();
        assertThat(course.getCreatedAt()).isNotNull();
        LocalDateTime first = course.getCreatedAt();
        course.onCreate();
        assertThat(course.getCreatedAt()).isAfterOrEqualTo(first);
    }

    @Test
    void preUpdate_SetsUpdatedAt() {
        Course course = new Course("ALG101", "Algorithms", "Desc", 5);
        assertThat(course.getUpdatedAt()).isNull();
        course.onUpdate();
        assertThat(course.getUpdatedAt()).isNotNull();
    }

    @Test
    void constructor_SetsBasicFields() {
        Course course = new Course("ALG101", "Algorithms", "Desc", 5);

        assertThat(course.getCourseCode()).isEqualTo("ALG101");
        assertThat(course.getCourseName()).isEqualTo("Algorithms");
        assertThat(course.getDescription()).isEqualTo("Desc");
        assertThat(course.getCredits()).isEqualTo(5);
        assertThat(course.getIsActive()).isTrue(); // default
    }
}
