package hr.algebra.uni_course_management.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GradeReportRow {
    private String studentFirstName;
    private String studentLastName;
    private String courseName;
    private Integer gradeValue;
    private String gradedAt;
}