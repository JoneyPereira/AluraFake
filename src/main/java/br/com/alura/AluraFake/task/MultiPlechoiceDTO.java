package br.com.alura.AluraFake.task;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.Set;

public class MultiPlechoiceDTO {
    private Long id;

    @NotBlank(message = "O enunciado é obrigatório")
    @Size(min = 10, message = "O enunciado deve ter no mínimo 10 caracteres")
    private String statement;

    @NotNull(message = "O ID do curso é obrigatório")
    private Long courseId;

    @NotNull(message = "A ordem é obrigatória")
    @Min(value = 1, message = "A ordem deve ser maior que zero")
    private Long order;

    @NotNull(message = "As opções são obrigatórias")
    @Size(min = 3, message = "A questão deve ter no mínimo 3 alternativas")
    @Valid
    private Set<Options> options;

    public MultiPlechoiceDTO() {
    }

    public MultiPlechoiceDTO(Long id, String statement, Long courseId, Long order, Set<Options> options) {
        this.id = id;
        this.statement = statement;
        this.courseId = courseId;
        this.order = order;
        this.options = options;
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

    public Set<Options> getOptions() {
        return options;
    }

    public void setOptions(Set<Options> options) {
        this.options = options;
    }

    public boolean hasExactlyOneCorrectOption() {
        if (options == null) return false;
        return options.stream().filter(Options::getCorrect).count() == 1;
    }

    public boolean hasAtLeastTwoCorrectOptions() {
        if (options == null) return false;
        long correctCount = options.stream().filter(Options::getCorrect).count();
        return correctCount >= 2;
    }
}
