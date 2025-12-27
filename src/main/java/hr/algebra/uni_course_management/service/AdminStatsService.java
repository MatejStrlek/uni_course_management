package hr.algebra.uni_course_management.service;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminStatsService {
    private final JdbcTemplate jdbcTemplate;

    public int getTotalUsers() {
        String sql = "SELECT COUNT(*) FROM app_user";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    public int getTotalCourses() {
        String sql = "SELECT COUNT(*) FROM course";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    public int getActiveCourses() {
        String sql = "SELECT COUNT(*) FROM course WHERE is_active = TRUE";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    public int getActiveUsers() {
        String sql = "SELECT COUNT(*) FROM app_user WHERE is_active = TRUE";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    public int getTotalStudents() {
        String sql = "SELECT COUNT(*) FROM app_user WHERE role_user = 'STUDENT'";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    public int getTotalProfessors() {
        String sql = "SELECT COUNT(*) FROM app_user WHERE role_user = 'PROFESSOR'";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    public int getTotalEnrollments() {
        String sql = "SELECT COUNT(*) FROM enrollment";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }
}