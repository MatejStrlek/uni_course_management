package hr.algebra.uni_course_management.controller.api;

import hr.algebra.uni_course_management.model.ScheduleEntry;
import hr.algebra.uni_course_management.service.AdminScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ScheduleRestController {
    private final AdminScheduleService adminScheduleService;

    @PostMapping("/schedules")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> createSchedule(@RequestParam Long courseId,
                                            @RequestParam DayOfWeek dayOfWeek,
                                            @RequestParam LocalTime startTime,
                                            @RequestParam LocalTime endTime,
                                            @RequestParam String room) {
        try {
            ScheduleEntry scheduleEntry = new ScheduleEntry();
            scheduleEntry.setDayOfWeek(dayOfWeek);
            scheduleEntry.setStartTime(startTime);
            scheduleEntry.setEndTime(endTime);
            scheduleEntry.setRoom(room);

            ScheduleEntry savedSchedule = adminScheduleService.createScheduleEntry(courseId, scheduleEntry);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Schedule entry created successfully");
            response.put("data", savedSchedule);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            error.put("data", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (RuntimeException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            error.put("data", null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Failed to create schedule entry: " + e.getMessage());
            error.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/courses/{id}/schedule")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ROLE_ADMIN')")
    public ResponseEntity<?> getCourseSchedule(@PathVariable Long id) {
        try {
            List<ScheduleEntry> schedules = adminScheduleService.findAllScheduleEntriesSorted().stream()
                    .filter(se -> se.getCourse().getId().equals(id))
                    .toList();
            Map<String, Object> response = new HashMap<>();

            if (schedules.isEmpty()) {
                response.put("success", false);
                response.put("message", "No schedule found for the specified course");
                response.put("data", null);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            response.put("success", true);
            response.put("message", "Schedule retrieved successfully");
            response.put("data", schedules);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Failed to retrieve schedule: " + e.getMessage());
            error.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/schedules")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getAllSchedules() {
        try {
            List<ScheduleEntry> schedules = adminScheduleService.findAllScheduleEntriesSorted();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Schedules retrieved successfully");
            response.put("data", schedules);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Failed to retrieve schedules: " + e.getMessage());
            error.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PutMapping("/schedules/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> updateSchedule(@PathVariable Long id,
                                            @RequestParam Long courseId,
                                            @RequestParam DayOfWeek dayOfWeek,
                                            @RequestParam LocalTime startTime,
                                            @RequestParam LocalTime endTime,
                                            @RequestParam String room) {
        try {
            ScheduleEntry updatedEntry = new ScheduleEntry();
            updatedEntry.setDayOfWeek(dayOfWeek);
            updatedEntry.setStartTime(startTime);
            updatedEntry.setEndTime(endTime);
            updatedEntry.setRoom(room);

            ScheduleEntry updatedSchedule = adminScheduleService.updateScheduleEntry(id, courseId, updatedEntry);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Schedule entry updated successfully");
            response.put("data", updatedSchedule);

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            error.put("data", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (RuntimeException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            error.put("data", null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Failed to update schedule entry: " + e.getMessage());
            error.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @DeleteMapping("/schedules/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteSchedule(@PathVariable Long id) {
        try {
            adminScheduleService.deleteScheduleEntry(id);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Schedule entry deleted successfully");
            response.put("data", null);

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            error.put("data", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Failed to delete schedule entry: " + e.getMessage());
            error.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}