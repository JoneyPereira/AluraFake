package br.com.alura.AluraFake.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

public class Options {
    private String option;
    @JsonProperty("isCorrect")
    private boolean correct;

    public Options() {}

    public Options(String option, boolean correct) {
        this.option = option;
        this.correct = correct;
    }


    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    @JsonProperty("isCorrect")
    public boolean isCorrect() {
        return correct;
    }

    @JsonSetter("isCorrect")
    public void setCorrect(boolean correct) {
        this.correct = correct;
    }
}
