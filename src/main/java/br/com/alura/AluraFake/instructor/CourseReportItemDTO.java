package br.com.alura.AluraFake.instructor;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.Status;

import java.time.LocalDateTime;

public class CourseReportItemDTO {
    private Long id;
    private String title;
    private Status status;
    private LocalDateTime publishedAt;
    private long totalTasks;

    public CourseReportItemDTO(Course course, long totalTasks) {
        this.id = course.getId();
        this.title = course.getTitle();
        this.status = course.getStatus();
        this.publishedAt = course.getPublishedAt();
        this.totalTasks = totalTasks;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Status getStatus() {
        return status;
    }

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public long getTotalTasks() {
        return totalTasks;
    }
}
