package hr.algebra.uni_course_management.repository;

import hr.algebra.uni_course_management.model.ScheduleEntry;
import hr.algebra.uni_course_management.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.List;

@Repository
public interface ScheduleEntryRepository extends CrudRepository<ScheduleEntry, Long> {
    List<ScheduleEntry> findAllByOrderByDayOfWeekAscStartTimeAsc();

    @Query("SELECT se FROM ScheduleEntry se " +
            "WHERE se.course.id IN (SELECT e.course.id FROM Enrollment e WHERE e.student = :student) " +
            "AND se.dayOfWeek = :dayOfWeek")
    List<ScheduleEntry> findByStudentAndDayOfWeek(@Param("student") User student, @Param("dayOfWeek") DayOfWeek dayOfWeek);
}