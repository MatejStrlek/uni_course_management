package hr.algebra.uni_course_management.service;

import hr.algebra.uni_course_management.model.Course;
import hr.algebra.uni_course_management.model.Enrollment;
import hr.algebra.uni_course_management.model.Grade;
import hr.algebra.uni_course_management.model.User;
import hr.algebra.uni_course_management.repository.GradeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GradeExportServiceTest {
    @Mock
    private GradeRepository gradeRepository;

    @InjectMocks
    private GradeExportService gradeExportService;

    private Grade buildGrade(String first, String last, String courseName,
                             Integer gradeValue, LocalDateTime gradedAt) {
        User student = new User();
        student.setFirstName(first);
        student.setLastName(last);

        Course course = new Course();
        course.setCourseName(courseName);

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);

        Grade grade = new Grade();
        grade.setEnrollment(enrollment);
        grade.setGradeValue(gradeValue);
        grade.setGradedAt(gradedAt);
        return grade;
    }

    @Test
    void exportGradesCsv_BuildsHeaderAndRows() {
        Grade g1 = buildGrade("John", "Doe", "Algorithms", 5,
                LocalDateTime.of(2024, 1, 1, 10, 0));
        Grade g2 = buildGrade("Jane", "Smith", "Databases", 4,
                null); // null date branch

        when(gradeRepository.getGradesForCourse(10L)).thenReturn(List.of(g1, g2));

        String csv = gradeExportService.exportGradesCsv(10L);

        String[] lines = csv.split("\n", -1);
        assertThat(lines[0])
                .isEqualTo("Student First Name,Student Last Name,Course Name,Grade Value,Graded At");
        assertThat(lines[1])
                .contains("John,Doe,Algorithms,5,2024-01-01T10:00");
        assertThat(lines[2])
                .isEqualTo("Jane,Smith,Databases,4,");
    }

    @Test
    void exportGradesCsv_EscapesCommasQuotesAndNewlines() {
        Grade g = buildGrade(
                "Jo,hn",               // comma
                "Do\"e",               // quote
                "Algo\nrithms",        // newline
                3,
                null
        );
        when(gradeRepository.getGradesForCourse(20L)).thenReturn(List.of(g));

        String csv = gradeExportService.exportGradesCsv(20L);

        String expected = """
            Student First Name,Student Last Name,Course Name,Grade Value,Graded At
            "Jo,hn","Do""e","Algo
            rithms",3,
            """;

        assertThat(csv).isEqualTo(expected);
    }
}