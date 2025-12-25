package hr.algebra.uni_course_management.service;

import hr.algebra.uni_course_management.model.Course;
import hr.algebra.uni_course_management.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;

    public List<Course> getAllCourses() {
        return (List<Course>) courseRepository.findAll();
    }

    public List<Course> getActiveCourses() {
        return courseRepository.findByIsActiveTrue();
    }

    public Course getCourseById(Long id) {
        return courseRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid course ID: " + id));
    }

    public Course getCourseByCode(String courseCode) {
        return courseRepository.findByCourseCode(courseCode).orElseThrow(() -> new IllegalArgumentException("Invalid course code" + courseCode));
    }

    public List<Course> getCoursesByProfessorId(Long professorId) {
        return courseRepository.findByProfessorIdAndIsActiveTrue(professorId);
    }

    public List<Course> searchCoursesByName(String courseName) {
        return courseRepository.searchByName(courseName);
    }

    public void createCourse(Course course) {
        if (courseRepository.existsByCourseCode(course.getCourseCode())) {
            throw new IllegalArgumentException("Course with code " + course.getCourseCode() + " already exists.");
        }
        courseRepository.save(course);
    }

    public void updateCourse(Long id, Course updatedCourse) {
        Course existingCourse = getCourseById(id);
        existingCourse.setCourseName(updatedCourse.getCourseName());
        existingCourse.setDescription(updatedCourse.getDescription());
        existingCourse.setCredits(updatedCourse.getCredits());
        existingCourse.setMaxStudents(updatedCourse.getMaxStudents());
        existingCourse.setSemester(updatedCourse.getSemester());
        existingCourse.setAcademicYear(updatedCourse.getAcademicYear());
        existingCourse.setIsActive(updatedCourse.getIsActive());
        existingCourse.setProfessor(updatedCourse.getProfessor());
        courseRepository.save(existingCourse);
    }

    public void deleteCourse(Long id) {
        Course existingCourse = getCourseById(id);
        courseRepository.delete(existingCourse);
    }

    public void deactivateCourse(Long id) {
        Course existingCourse = getCourseById(id);
        existingCourse.setIsActive(false);
        courseRepository.save(existingCourse);
    }
}