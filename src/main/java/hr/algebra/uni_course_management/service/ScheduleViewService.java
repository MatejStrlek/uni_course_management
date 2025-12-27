package hr.algebra.uni_course_management.service;

import hr.algebra.uni_course_management.dto.ScheduleRow;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleViewService {
    private final JdbcTemplate jdbcTemplate;

    public List<ScheduleRow> getProfessorSchedule(Long professorId) {
        String sql = """
                SELECT se.day_of_week, se.start_time, se.end_time, c.course_name, c.course_code, se.room
                FROM schedule_entry se
                JOIN course c ON se.course_id = c.id
                WHERE c.professor_id = ? AND c.is_active = TRUE
                ORDER BY se.day_of_week, se.start_time
                    \s""";

        return jdbcTemplate.query(
                sql,
                new Object[]{professorId},
                (rs, rowNum) -> new ScheduleRow(
                        rs.getString("day_of_week"),
                        rs.getTime("start_time").toLocalTime(),
                        rs.getTime("end_time").toLocalTime(),
                        rs.getString("course_name"),
                        rs.getString("course_code"),
                        rs.getString("room")
                )
        );
    }

    public List<ScheduleRow> getStudentSchedule(Long studentId) {
        String sql = """
                SELECT se.day_of_week, se.start_time, se.end_time, c.course_name, c.course_code, se.room
                FROM schedule_entry se
                JOIN course c ON se.course_id = c.id
                JOIN enrollment e ON c.id = e.course_id
                WHERE e.student_id = ? AND e.status = 'ENROLLED' AND c.is_active = TRUE
                ORDER BY se.day_of_week, se.start_time
                   \s""";

        return jdbcTemplate.query(
                sql,
                new Object[]{studentId},
                (rs, rowNum) -> new ScheduleRow(
                        rs.getString("day_of_week"),
                        rs.getTime("start_time").toLocalTime(),
                        rs.getTime("end_time").toLocalTime(),
                        rs.getString("course_name"),
                        rs.getString("course_code"),
                        rs.getString("room")
                )
        );
    }
}