package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    @Query("SELECT COUNT(t) > 0 FROM Task t WHERE t.course = :course AND t.statement = :statement")
    boolean existsByStatementAndCourse(@Param("statement") String statement, @Param("course") Course course);

    @Query("SELECT COUNT(t) > 0 FROM Task t WHERE t.course = :course AND t.order = :order")
    boolean existsByOrderAndCourse(@Param("order") Long order, @Param("course") Course course);

    @Query("SELECT COUNT(t) > 0 FROM Task t WHERE t.course = :course AND t.status = :type")
    boolean existsByCourseAndType(@Param("course") Course course, @Param("type") Type type);

    @Query("SELECT t FROM Task t WHERE t.course = :course ORDER BY t.order")
    List<Task> findByCourseOrderByOrderAsc(@Param("course") Course course);

    @Query("SELECT COUNT(DISTINCT t.status) FROM Task t WHERE t.course = :course")
    long countDistinctTypesByCourse(@Param("course") Course course);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.course.id = :courseId")
    long countByCourseId(@Param("courseId") Long courseId);
}
