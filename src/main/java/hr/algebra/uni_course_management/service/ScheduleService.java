package hr.algebra.uni_course_management.service;

import hr.algebra.uni_course_management.model.Course;
import hr.algebra.uni_course_management.model.ScheduleEntry;
import hr.algebra.uni_course_management.repository.CourseRepository;
import hr.algebra.uni_course_management.repository.ScheduleEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ScheduleService {
    private final ScheduleEntryRepository scheduleEntryRepository;
    private final CourseRepository courseRepository;

    public List<ScheduleEntry> findAllScheduleEntriesSorted() {
        return scheduleEntryRepository.findAllByOrderByDayOfWeekAscStartTimeAsc();
    }

    public ScheduleEntry getScheduleEntryById(Long id) {
        return scheduleEntryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Schedule entry with ID " + id + " not found."));
    }

    public void createScheduleEntry(Long courseId, ScheduleEntry scheduleEntry) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course with ID " + courseId + " not found."));
        scheduleEntry.setCourse(course);
        scheduleEntryRepository.save(scheduleEntry);
    }

    public void updateScheduleEntry(Long id, Long courseId, ScheduleEntry updatedEntry) {
        ScheduleEntry existingEntry = getScheduleEntryById(id);
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course with ID " + courseId + " not found."));

        existingEntry.setCourse(course);
        existingEntry.setDayOfWeek(updatedEntry.getDayOfWeek());
        existingEntry.setStartTime(updatedEntry.getStartTime());
        existingEntry.setEndTime(updatedEntry.getEndTime());
        existingEntry.setRoom(updatedEntry.getRoom());

        scheduleEntryRepository.save(existingEntry);
    }

    public void deleteScheduleEntry(Long id) {
        if (!scheduleEntryRepository.existsById(id)) {
            throw new IllegalArgumentException("Schedule entry with ID " + id + " not found.");
        }
        scheduleEntryRepository.deleteById(id);
    }
}