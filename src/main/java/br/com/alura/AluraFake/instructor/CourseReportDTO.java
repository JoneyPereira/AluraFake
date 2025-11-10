package br.com.alura.AluraFake.instructor;

import java.util.List;

public class CourseReportDTO {
    private List<CourseReportItemDTO> courses;
    private long totalPublishedCourses;

    public CourseReportDTO(List<CourseReportItemDTO> courses, long totalPublishedCourses) {
        this.courses = courses;
        this.totalPublishedCourses = totalPublishedCourses;
    }

    public List<CourseReportItemDTO> getCourses() {
        return courses;
    }

    public long getTotalPublishedCourses() {
        return totalPublishedCourses;
    }
}
