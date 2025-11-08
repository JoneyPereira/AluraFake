package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime createdAt = LocalDateTime.now();
    private String statement;

    @ManyToOne
    @JoinColumn(name = "id")
    private Course course;

    @Enumerated(EnumType.STRING)
    private Type type;
    private LocalDateTime publishedAt;

    private Long order;

    @Deprecated
    public Task(){}

    public Task(String statement, Course course, Type type, Long order) {
        this.statement = statement;
        this.course = course;
        this.type = type;
        this.order = order;
    }
}
