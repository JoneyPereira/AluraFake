package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.CourseRepository;
import br.com.alura.AluraFake.course.Status;
import br.com.alura.AluraFake.util.ErrorItemDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/task/new")
public class TaskController {

    private final TaskRepository taskRepository;
    private final CourseRepository courseRepository;

    @Autowired
    public TaskController(TaskRepository taskRepository, CourseRepository courseRepository) {
        this.taskRepository = taskRepository;
        this.courseRepository = courseRepository;
    }

    @PostMapping("/opentext")
    public ResponseEntity<?> newOpenTextExercise(@Valid @RequestBody OpenTextDTO opentext) {
        Optional<Course> courseOptional = courseRepository.findById(opentext.getCourseId());
        if (courseOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorItemDTO("courseId", "Curso não encontrado, id: " + opentext.getCourseId()));
        }
        Course courseEntity = courseOptional.get();

        if (courseEntity.getStatus() != Status.BUILDING) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorItemDTO("courseId", "O curso precisa estar com status BUILDING para receber atividades"));
        }

        if (taskRepository.existsByStatementAndCourse(opentext.getStatement(), courseEntity)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorItemDTO("statement", "Já existe uma tarefa com este enunciado no curso"));
        }

        Task task = new Task(opentext.getStatement(), courseEntity, opentext.getOrder());
        task.setStatus(Type.OPEN_TEXT);

        taskRepository.save(task);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(task.getId())
                .toUri();

        return ResponseEntity.created(uri).body(task);
    }

    @PostMapping("/singlechoice")
    public ResponseEntity<?> newSingleChoice() {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/multiplechoice")
    public ResponseEntity<?> newMultipleChoice() {
        return ResponseEntity.ok().build();
    }

}