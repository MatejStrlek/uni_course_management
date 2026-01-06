package hr.algebra.uni_course_management.service;

import hr.algebra.uni_course_management.dto.ScheduleRow;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.Time;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScheduleViewServiceTest {
    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private ScheduleViewService scheduleViewService;

    @Test
    void getProfessorSchedule_UsesCorrectSqlAndParams() {
        @SuppressWarnings("unchecked")
        ArgumentCaptor<RowMapper<ScheduleRow>> mapperCaptor =
                ArgumentCaptor.forClass(RowMapper.class);

        when(jdbcTemplate.query(anyString(), any(Object[].class), any(RowMapper.class)))
                .thenReturn(List.of(
                        new ScheduleRow(
                                "MONDAY",
                                LocalTime.of(10, 0),
                                LocalTime.of(12, 0),
                                "Algorithms",
                                "ALG101",
                                "A1"
                        )
                ));

        List<ScheduleRow> result = scheduleViewService.getProfessorSchedule(42L);

        assertThat(result).hasSize(1);
        ScheduleRow row = result.get(0);
        assertThat(row.getDayOfWeek()).isEqualTo("MONDAY");
        assertThat(row.getCourseName()).isEqualTo("Algorithms");

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Object[]> argsCaptor = ArgumentCaptor.forClass(Object[].class);

        verify(jdbcTemplate).query(sqlCaptor.capture(), argsCaptor.capture(), mapperCaptor.capture());

        String usedSql = sqlCaptor.getValue();
        assertThat(usedSql).contains("FROM schedule_entry se");
        assertThat(usedSql).contains("WHERE c.professor_id = ?");
        assertThat(argsCaptor.getValue()).containsExactly(42L);
    }

    @Test
    void getStudentSchedule_UsesCorrectSqlAndParams() {
        when(jdbcTemplate.query(anyString(), any(Object[].class), any(RowMapper.class)))
                .thenReturn(List.of());

        List<ScheduleRow> result = scheduleViewService.getStudentSchedule(7L);

        assertThat(result).isEmpty();

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Object[]> argsCaptor = ArgumentCaptor.forClass(Object[].class);

        verify(jdbcTemplate).query(sqlCaptor.capture(), argsCaptor.capture(), any(RowMapper.class));

        String usedSql = sqlCaptor.getValue();
        assertThat(usedSql).contains("JOIN enrollment e ON c.id = e.course_id");
        assertThat(usedSql).contains("WHERE e.student_id = ?");
        assertThat(argsCaptor.getValue()).containsExactly(7L);
    }

    @Test
    void rowMapper_MapsResultSetCorrectly() throws Exception {
        ResultSet rs = mock(ResultSet.class);
        when(rs.getString("day_of_week")).thenReturn("FRIDAY");
        when(rs.getTime("start_time")).thenReturn(Time.valueOf(LocalTime.of(8, 0)));
        when(rs.getTime("end_time")).thenReturn(Time.valueOf(LocalTime.of(9, 30)));
        when(rs.getString("course_name")).thenReturn("Databases");
        when(rs.getString("course_code")).thenReturn("DB201");
        when(rs.getString("room")).thenReturn("B2");

        @SuppressWarnings("unchecked")
        ArgumentCaptor<RowMapper<ScheduleRow>> mapperCaptor =
                ArgumentCaptor.forClass(RowMapper.class);

        when(jdbcTemplate.query(anyString(), any(Object[].class), mapperCaptor.capture()))
                .thenReturn(List.of());

        scheduleViewService.getProfessorSchedule(1L);

        RowMapper<ScheduleRow> mapper = mapperCaptor.getValue();
        ScheduleRow mapped = mapper.mapRow(rs, 0);

        assertThat(mapped.getDayOfWeek()).isEqualTo("FRIDAY");
        assertThat(mapped.getStartTime()).isEqualTo(LocalTime.of(8, 0));
        assertThat(mapped.getEndTime()).isEqualTo(LocalTime.of(9, 30));
        assertThat(mapped.getCourseName()).isEqualTo("Databases");
        assertThat(mapped.getCourseCode()).isEqualTo("DB201");
        assertThat(mapped.getRoom()).isEqualTo("B2");
    }
}