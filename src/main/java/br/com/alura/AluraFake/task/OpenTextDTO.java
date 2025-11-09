package br.com.alura.AluraFake.task;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OpenTextDTO {
    private Long id;
    private String statement;

    @JsonProperty("courseId")
    private Long course_id;
    private Long order;

    public OpenTextDTO() {
    }

    public OpenTextDTO(Long id, String statement, Long course_id, Long order) {
        this.id = id;
        this.statement = statement;
        this.course_id = course_id;
        this.order = order;
    }

    public OpenTextDTO(String statement, Long course_id, Long order) {
        this.statement = statement;
        this.course_id = course_id;
        this.order = order;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatement() {
        return statement;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public Long getCourse_id() {
        return course_id;
    }

    public void setCourse_id(Long course_id) {
        this.course_id = course_id;
    }

    public Long getOrder() {
        return order;
    }

    public void setOrder(Long order) {
        this.order = order;
    }
}
