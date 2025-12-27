package hr.algebra.uni_course_management.repository;

import hr.algebra.uni_course_management.model.ScheduleEntry;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleEntryRepository extends CrudRepository<ScheduleEntry, Long> {
    List<ScheduleEntry> findAllByOrderByDayOfWeekAscStartTimeAsc();
}