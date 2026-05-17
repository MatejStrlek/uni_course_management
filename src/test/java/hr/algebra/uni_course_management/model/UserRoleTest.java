package hr.algebra.uni_course_management.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class UserRoleTest {
    @Test
    void valueOf_ParsesAllDefinedRoles() {
        assertThat(UserRole.valueOf("STUDENT")).isEqualTo(UserRole.STUDENT);
        assertThat(UserRole.valueOf("PROFESSOR")).isEqualTo(UserRole.PROFESSOR);
        assertThat(UserRole.valueOf("ADMIN")).isEqualTo(UserRole.ADMIN);
    }
}
