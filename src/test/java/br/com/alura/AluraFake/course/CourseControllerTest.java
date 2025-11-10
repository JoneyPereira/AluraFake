package br.com.alura.AluraFake.course;

import br.com.alura.AluraFake.task.Task;
import br.com.alura.AluraFake.task.TaskRepository;
import br.com.alura.AluraFake.task.Type;
import br.com.alura.AluraFake.user.Role;
import br.com.alura.AluraFake.user.User;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private EntityManager entityManager;

    private Course course;
    private User instructor;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
        courseRepository.deleteAll();

        instructor = createInstructor();
        course = createCourse(instructor);
    }

    private User createInstructor() {
        User instructor = new User();
        instructor.setName("Test Instructor");
        instructor.setEmail("instructor@test.com");
        instructor.setPassword("123456");
        instructor.setRole(Role.INSTRUCTOR);
        entityManager.persist(instructor);
        return instructor;
    }

    private Course createCourse(User instructor) {
        Course course = new Course("Java Test Course", "Curso de teste para Java", instructor);
        return courseRepository.save(course);
    }

    @Test
    void shouldNotAllowPublishingCourseWithoutAllActivityTypes() throws Exception {
        // Create only one type of activity
        Task task = new Task("What is Java?", course, 1L);
        task.setStatus(Type.OPEN_TEXT);
        taskRepository.save(task);

        mockMvc.perform(post("/course/{id}/publish", course.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.field").value("activities"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("O curso deve ter pelo menos uma atividade de cada tipo"));
    }

    @Test
    void shouldNotAllowPublishingCourseWithNonSequentialOrder() throws Exception {
        // Create activities with gap in order
        Task task1 = new Task("What is Java?", course, 1L);
        task1.setStatus(Type.OPEN_TEXT);
        taskRepository.save(task1);

        Task task2 = new Task("What is Spring?", course, 3L); // Gap here (missing order 2)
        task2.setStatus(Type.SINGLE_CHOICE);
        task2.setOptions(Set.of());
        taskRepository.save(task2);

        Task task3 = new Task("What is JPA?", course, 4L);
        task3.setStatus(Type.MULTIPLE_CHOICE);
        task3.setOptions(Set.of());
        taskRepository.save(task3);

        mockMvc.perform(post("/course/{id}/publish", course.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.field").value("order"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("As atividades devem estar em ordem sequencial contínua"));
    }

    @Test
    void shouldNotAllowPublishingNonBuildingCourse() throws Exception {
        course.setStatus(Status.PUBLISHED);
        courseRepository.save(course);

        mockMvc.perform(post("/course/{id}/publish", course.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.field").value("status"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("O curso precisa estar com status BUILDING para ser publicado"));
    }

    @Test
    void shouldPublishCourseSuccessfully() throws Exception {
        // Create one activity of each type in sequential order
        Task task1 = new Task("What is Java?", course, 1L);
        task1.setStatus(Type.OPEN_TEXT);
        taskRepository.save(task1);

        Task task2 = new Task("What is Spring?", course, 2L);
        task2.setStatus(Type.SINGLE_CHOICE);
        task2.setOptions(Set.of());
        taskRepository.save(task2);

        Task task3 = new Task("What is JPA?", course, 3L);
        task3.setStatus(Type.MULTIPLE_CHOICE);
        task3.setOptions(Set.of());
        taskRepository.save(task3);

        mockMvc.perform(post("/course/{id}/publish", course.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
