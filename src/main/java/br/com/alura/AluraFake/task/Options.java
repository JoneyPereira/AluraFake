package br.com.alura.AluraFake.task;

public class Options {
    private String option;
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

    public boolean getCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public boolean isCorrect() {
        return correct;
    }
}
