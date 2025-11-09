package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime createdAt = LocalDateTime.now();
    private String statement;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Course course;

    @Enumerated(EnumType.STRING)
    private Type status;

    private LocalDateTime publishedAt;

    @Column(name = "task_order")
    private Long order;

    @Column(columnDefinition = "json")
    @Convert(converter = OptionsSetConverter.class)
    private Set<Options> options = new HashSet<>();

    public Task(String statement, Course course, Type status, Long order) {
        this.statement = statement;
        this.course = course;
        this.status = status;
        this.order = order;
    }

    public Task(String statement, Course course, Long order) {
        this.statement = statement;
        this.course = course;
        this.order = order;
    }
}
