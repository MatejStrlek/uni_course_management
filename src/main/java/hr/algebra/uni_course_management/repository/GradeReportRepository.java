package hr.algebra.uni_course_management.repository;

import hr.algebra.uni_course_management.dto.GradeReportRow;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class GradeReportRepository {
    private final JdbcTemplate jdbcTemplate;

    public List<GradeReportRow> getGradesForCourse(Long courseId) {
        String sql = """
                SELECT u.first_name AS studentFirstName,
                       u.last_name AS studentLastName,
                       c.course_name AS courseName,
                       g.grade_value AS gradeValue,
                       TO_CHAR(g.graded_at, 'YYYY-MM-DD HH24:MI') AS gradedAt
                FROM grades g
                JOIN enrollments e ON g.enrollment_id = e.id
                JOIN users u ON e.student_id = u.id
                JOIN courses c ON e.course_id = c.id
                WHERE c.id = ?
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            GradeReportRow row = new GradeReportRow();
            row.setStudentFirstName(rs.getString("studentFirstName"));
            row.setStudentLastName(rs.getString("studentLastName"));
            row.setCourseName(rs.getString("courseName"));
            row.setGradeValue((Integer) rs.getObject("gradeValue"));
            row.setGradedAt(rs.getTimestamp("gradedAt") != null
                    ? String.valueOf(rs.getTimestamp("gradedAt").toLocalDateTime())
                    : null);
            return row;
        }, courseId);
    }
}