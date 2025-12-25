package hr.algebra.uni_course_management.service;

import hr.algebra.uni_course_management.dto.GradeReportRow;
import hr.algebra.uni_course_management.model.Grade;
import hr.algebra.uni_course_management.repository.GradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GradeExportService {
    private final GradeRepository gradeRepository;

    public String exportGradesCsv(Long courseId) {
        List<Grade> grades = gradeRepository.getGradesForCourse(courseId);
        StringBuilder csvBuilder = new StringBuilder();
        csvBuilder.append("Student First Name,Student Last Name,Course Name,Grade Value,Graded At\n");

        List<GradeReportRow> gradeReportRows = grades
                .stream()
                .map(grade -> {
                    GradeReportRow row = new GradeReportRow();
                    row.setStudentFirstName(grade.getEnrollment().getStudent().getFirstName());
                    row.setStudentLastName(grade.getEnrollment().getStudent().getLastName());
                    row.setCourseName(grade.getEnrollment().getCourse().getCourseName());
                    row.setGradeValue(grade.getGradeValue());
                    row.setGradedAt(grade.getGradedAt() != null ? grade.getGradedAt().toString() : null);
                    return row;
                })
                .toList();

        for (GradeReportRow row : gradeReportRows) {
            csvBuilder.append(escapeCsv(row.getStudentFirstName())).append(",");
            csvBuilder.append(escapeCsv(row.getStudentLastName())).append(",");
            csvBuilder.append(escapeCsv(row.getCourseName())).append(",");
            csvBuilder.append(row.getGradeValue() != null ? row.getGradeValue() : "").append(",");
            csvBuilder.append(row.getGradedAt() != null ? row.getGradedAt() : "").append("\n");
        }

        return csvBuilder.toString();
    }

    private String escapeCsv(String value) {
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            value = value.replace("\"", "\"\"");
            return "\"" + value + "\"";
        }
        return value;
    }
}