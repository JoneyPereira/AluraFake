package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.CourseRepository;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class TaskOrderTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private EntityManager entityManager;

    private Course course;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
        courseRepository.deleteAll();

        User instructor = createInstructor();
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

    private String createOpenTextRequest(Long courseId, String statement, Long order) {
        return String.format("""
                {
                    "courseId": %d,
                    "statement": "%s",
                    "order": %d
                }
                """, courseId, statement, order);
    }

    @Test
    void shouldReorderTasksWhenInsertingInMiddle() throws Exception {
        // Criar duas tarefas: ordem 1 e 2
        mockMvc.perform(post("/task/new/opentext")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createOpenTextRequest(course.getId(), "Primeira tarefa", 1L)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/task/new/opentext")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createOpenTextRequest(course.getId(), "Segunda tarefa", 2L)))
                .andExpect(status().isCreated());

        // Inserir uma nova tarefa na ordem 1
        mockMvc.perform(post("/task/new/opentext")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createOpenTextRequest(course.getId(), "Nova tarefa no meio", 1L)))
                .andExpect(status().isCreated());

        // Verificar se as ordens foram atualizadas corretamente
        List<Task> tasks = taskRepository.findByCourseOrderByOrderAsc(course);
        assertEquals(3, tasks.size());
        assertEquals("Nova tarefa no meio", tasks.get(0).getStatement());
        assertEquals(1L, tasks.get(0).getOrder());
        assertEquals("Primeira tarefa", tasks.get(1).getStatement());
        assertEquals(2L, tasks.get(1).getOrder());
        assertEquals("Segunda tarefa", tasks.get(2).getStatement());
        assertEquals(3L, tasks.get(2).getOrder());
    }

    @Test
    void shouldNotAllowGapsInTaskOrder() throws Exception {
        // Criar primeira tarefa com ordem 1
        mockMvc.perform(post("/task/new/opentext")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createOpenTextRequest(course.getId(), "Primeira tarefa", 1L)))
                .andExpect(status().isCreated());

        // Tentar criar tarefa com ordem 3 (pulando 2)
        mockMvc.perform(post("/task/new/opentext")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createOpenTextRequest(course.getId(), "Tarefa com gap", 3L)))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.field").value("order"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("A ordem deve ser sequencial. Próxima ordem válida: 2"));
    }

    @Test
    void shouldRequireFirstTaskToHaveOrderOne() throws Exception {
        // Tentar criar primeira tarefa com ordem 2
        mockMvc.perform(post("/task/new/opentext")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createOpenTextRequest(course.getId(), "Primeira tarefa", 2L)))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.field").value("order"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("A primeira atividade deve ter ordem 1"));
    }
}
