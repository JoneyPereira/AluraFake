package br.com.alura.AluraFake.instructor;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.CourseRepository;
import br.com.alura.AluraFake.course.Status;
import br.com.alura.AluraFake.task.TaskRepository;
import br.com.alura.AluraFake.user.Role;
import br.com.alura.AluraFake.user.User;
import br.com.alura.AluraFake.user.UserRepository;
import br.com.alura.AluraFake.util.ErrorItemDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/instructor")
public class InstructorController {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final CourseRepository courseRepository;

    public InstructorController(UserRepository userRepository,
                                TaskRepository taskRepository,
                                CourseRepository courseRepository) {
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.courseRepository = courseRepository;
    }

    @GetMapping("/{id}/courses")
    public ResponseEntity<Object> listCourses(@PathVariable Long id) {
        Optional<User> userOptional = userRepository.findById(id);

        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorItemDTO("id", "Usuário não encontrado"));
        }

        User user = userOptional.get();
        if (user.getRole() != Role.INSTRUCTOR) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorItemDTO("role", "Usuário não é um instrutor"));
        }

        List<Course> instructorCourses = courseRepository.findByInstructorId(id);

        List<CourseReportItemDTO> courseReport = instructorCourses.stream()
                .map(course -> {
                    long totalTasks = taskRepository.countByCourseId(course.getId());
                    return new CourseReportItemDTO(course, totalTasks);
                })
                .toList();

        long totalPublishedCourses = instructorCourses.stream()
                .filter(course -> course.getStatus() == Status.PUBLISHED)
                .count();

        CourseReportDTO report = new CourseReportDTO(courseReport, totalPublishedCourses);
        return ResponseEntity.ok(report);
    }

}
