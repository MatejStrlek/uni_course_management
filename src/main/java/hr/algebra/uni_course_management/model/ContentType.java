package hr.algebra.uni_course_management.model;

public enum ContentType {
    LECTURE,
    ASSIGNMENT,
    QUIZ,
    READING_MATERIAL,
    ANNOUNCEMENT,
    OTHER;

    public String getDisplayName() {
        return switch (this) {
            case LECTURE -> "Lecture";
            case ASSIGNMENT -> "Assignment";
            case QUIZ -> "Quiz";
            case READING_MATERIAL -> "Reading Material";
            case ANNOUNCEMENT -> "Announcement";
            case OTHER -> "Other";
        };
    }
}