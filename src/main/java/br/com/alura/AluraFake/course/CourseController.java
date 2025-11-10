package br.com.alura.AluraFake.course;

import br.com.alura.AluraFake.task.Task;
import br.com.alura.AluraFake.task.TaskRepository;
import br.com.alura.AluraFake.task.Type;
import br.com.alura.AluraFake.user.*;
import br.com.alura.AluraFake.util.ErrorItemDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.*;

@RestController
@RequestMapping("/course")
public class CourseController {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    @Autowired
    public CourseController(CourseRepository courseRepository, UserRepository userRepository, TaskRepository taskRepository){
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
    }

    @Transactional
    @PostMapping("/new")
    public ResponseEntity createCourse(@Valid @RequestBody NewCourseDTO newCourse) {

        //Caso implemente o bonus, pegue o instrutor logado
        Optional<User> possibleAuthor = userRepository
                .findByEmail(newCourse.getEmailInstructor())
                .filter(User::isInstructor);

        if(possibleAuthor.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorItemDTO("emailInstructor", "Usuário não é um instrutor"));
        }

        Course course = new Course(newCourse.getTitle(), newCourse.getDescription(), possibleAuthor.get());

        courseRepository.save(course);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/all")
    public ResponseEntity<List<CourseListItemDTO>> createCourse() {
        List<CourseListItemDTO> courses = courseRepository.findAll().stream()
                .map(CourseListItemDTO::new)
                .toList();
        return ResponseEntity.ok(courses);
    }

    @Transactional
    @PostMapping("/{id}/publish")
    public ResponseEntity<?> publishCourse(@PathVariable Long id) {
        Optional<Course> courseOptional = courseRepository.findById(id);
        if (courseOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorItemDTO("id", "Curso não encontrado, id: " + id));
        }

        Course course = courseOptional.get();

        if (!course.isStatusBuilding()) {
            return ResponseEntity.badRequest()
                    .body(new ErrorItemDTO("status", "O curso precisa estar com status BUILDING para ser publicado"));
        }

        if (!hasAllRequiredActivityTypes(course)) {
            return ResponseEntity.badRequest()
                    .body(new ErrorItemDTO("activities", "O curso deve ter pelo menos uma atividade de cada tipo"));
        }

        if (!hasSequentialOrder(course)) {
            return ResponseEntity.badRequest()
                    .body(new ErrorItemDTO("order", "As atividades devem estar em ordem sequencial contínua"));
        }

        course.publish();
        courseRepository.save(course);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(course.getId())
                .toUri();

        return ResponseEntity.created(uri).body(course);
    }

    private boolean hasAllRequiredActivityTypes(Course course) {
        return taskRepository.countDistinctTypesByCourse(course) == Type.values().length;
    }

    private boolean hasSequentialOrder(Course course) {
        List<Task> tasks = taskRepository.findByCourseOrderByOrderAsc(course);
        if (tasks.isEmpty()) return false;

        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getOrder() != i + 1) {
                return false;
            }
        }
        return true;
    }
}
