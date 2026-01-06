package hr.algebra.uni_course_management.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class UserTest {
    @Test
    void onCreate_SetsCreatedAt() {
        User user = new User();
        assertThat(user.getCreatedAt()).isNull();
        user.onCreate();
        assertThat(user.getCreatedAt()).isNotNull();
    }

    @Test
    void onUpdate_SetsUpdatedAt() {
        User user = new User();
        assertThat(user.getUpdatedAt()).isNull();
        user.onUpdate();
        assertThat(user.getUpdatedAt()).isNotNull();
    }

    @Test
    void constructor_SetsFieldsAndDefaults() {
        User user = new User(
                "john",
                "encodedPass",
                "John",
                "Doe",
                "john@example.com",
                UserRole.STUDENT
        );

        assertThat(user.getUsername()).isEqualTo("john");
        assertThat(user.getPassword()).isEqualTo("encodedPass");
        assertThat(user.getFirstName()).isEqualTo("John");
        assertThat(user.getLastName()).isEqualTo("Doe");
        assertThat(user.getEmail()).isEqualTo("john@example.com");
        assertThat(user.getRole()).isEqualTo(UserRole.STUDENT);
        assertThat(user.getIsActive()).isTrue(); // default value
    }

    @Test
    void getFullName_ConcatenatesFirstAndLastName() {
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");

        String fullName = user.getFullName();
        assertThat(fullName).isEqualTo("John Doe");
    }
}