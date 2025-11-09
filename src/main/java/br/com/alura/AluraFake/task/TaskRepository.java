package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TaskRepository extends JpaRepository<Task, Long> {
    @Query("SELECT COUNT(t) > 0 FROM Task t WHERE t.course = :course AND t.statement = :statement")
    boolean existsByStatementAndCourse(@Param("statement") String statement, @Param("course") Course course);

    @Query("SELECT COUNT(t) > 0 FROM Task t WHERE t.course = :course AND t.order = :order")
    boolean existsByOrderAndCourse(@Param("order") Long order, @Param("course") Course course);
}
