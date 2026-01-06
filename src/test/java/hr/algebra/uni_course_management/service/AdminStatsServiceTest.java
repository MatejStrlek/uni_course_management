package hr.algebra.uni_course_management.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminStatsServiceTest {
    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private AdminStatsService adminStatsService;

    @Test
    void getTotalUsers_UsesCorrectSql() {
        when(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM app_user", Integer.class))
                .thenReturn(5);

        int result = adminStatsService.getTotalUsers();

        assertThat(result).isEqualTo(5);
        verify(jdbcTemplate).queryForObject("SELECT COUNT(*) FROM app_user", Integer.class);
    }

    @Test
    void getTotalCourses_UsesCorrectSql() {
        when(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM course", Integer.class))
                .thenReturn(3);

        int result = adminStatsService.getTotalCourses();

        assertThat(result).isEqualTo(3);
        verify(jdbcTemplate).queryForObject("SELECT COUNT(*) FROM course", Integer.class);
    }

    @Test
    void getActiveCourses_UsesCorrectSql() {
        when(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM course WHERE is_active = TRUE", Integer.class))
                .thenReturn(2);

        int result = adminStatsService.getActiveCourses();

        assertThat(result).isEqualTo(2);
        verify(jdbcTemplate).queryForObject("SELECT COUNT(*) FROM course WHERE is_active = TRUE", Integer.class);
    }

    @Test
    void getActiveUsers_UsesCorrectSql() {
        when(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM app_user WHERE is_active = TRUE", Integer.class))
                .thenReturn(4);

        int result = adminStatsService.getActiveUsers();

        assertThat(result).isEqualTo(4);
        verify(jdbcTemplate).queryForObject("SELECT COUNT(*) FROM app_user WHERE is_active = TRUE", Integer.class);
    }

    @Test
    void getTotalStudents_UsesCorrectSql() {
        when(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM app_user WHERE role_user = 'STUDENT'", Integer.class))
                .thenReturn(10);

        int result = adminStatsService.getTotalStudents();

        assertThat(result).isEqualTo(10);
        verify(jdbcTemplate).queryForObject("SELECT COUNT(*) FROM app_user WHERE role_user = 'STUDENT'", Integer.class);
    }

    @Test
    void getTotalProfessors_UsesCorrectSql() {
        when(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM app_user WHERE role_user = 'PROFESSOR'", Integer.class))
                .thenReturn(2);

        int result = adminStatsService.getTotalProfessors();

        assertThat(result).isEqualTo(2);
        verify(jdbcTemplate).queryForObject("SELECT COUNT(*) FROM app_user WHERE role_user = 'PROFESSOR'", Integer.class);
    }

    @Test
    void getTotalEnrollments_UsesCorrectSql() {
        when(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM enrollment", Integer.class))
                .thenReturn(7);

        int result = adminStatsService.getTotalEnrollments();

        assertThat(result).isEqualTo(7);
        verify(jdbcTemplate).queryForObject("SELECT COUNT(*) FROM enrollment", Integer.class);
    }
}