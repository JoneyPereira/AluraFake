package br.com.alura.AluraFake.task;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Options {
    @NotBlank(message = "O texto da opção não pode estar vazio")
    private String option;
    private Boolean isCorrect;

    public Options() {
    }

    public Options(String option, Boolean isCorrect) {
        this.option = option;
        this.isCorrect = isCorrect;
    }

    public Boolean getCorrect() {
        return isCorrect;
    }

    public void setCorrect(Boolean correct) {
        isCorrect = correct;
    }
}
