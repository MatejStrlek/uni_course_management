package hr.algebra.uni_course_management.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalTime;

@Getter
@AllArgsConstructor
public class ScheduleRow {
    private String dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private String courseName;
    private String courseCode;
    private String room;
}