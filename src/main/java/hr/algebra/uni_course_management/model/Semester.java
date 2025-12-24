package hr.algebra.uni_course_management.model;

public enum Semester {
    WINTER,
    SUMMER;

    public String getDisplayName() {
        return switch (this) {
            case WINTER -> "Winter semester";
            case SUMMER -> "Summer semester";
        };
    }
}