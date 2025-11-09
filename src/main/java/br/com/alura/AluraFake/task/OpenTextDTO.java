package br.com.alura.AluraFake.task;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OpenTextDTO {
    private Long id;

    @jakarta.validation.constraints.NotBlank(message = "O enunciado é obrigatório")
    @jakarta.validation.constraints.Size(min = 10, message = "O enunciado deve ter no mínimo 10 caracteres")
    private String statement;

    @JsonProperty("courseId")
    @jakarta.validation.constraints.NotNull(message = "O ID do curso é obrigatório")
    private Long courseId;

    @jakarta.validation.constraints.NotNull(message = "A ordem é obrigatória")
    @jakarta.validation.constraints.Min(value = 1, message = "A ordem deve ser maior que zero")
    @jakarta.validation.constraints.Positive(message = "A ordem deve ser um número positivo")
    private Long order;

    public OpenTextDTO() {
    }

    public OpenTextDTO(Long id, String statement, Long courseId, Long order) {
        this.id = id;
        this.statement = statement;
        this.courseId = courseId;
        this.order = order;
    }

    public OpenTextDTO(String statement, Long courseId, Long order) {
        this.statement = statement;
        this.courseId = courseId;
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

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Long getOrder() {
        return order;
    }

    public void setOrder(Long order) {
        this.order = order;
    }
}
