package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.CourseRepository;
import br.com.alura.AluraFake.course.Status;
import br.com.alura.AluraFake.user.Role;
import br.com.alura.AluraFake.user.User;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class TaskControllerTest {

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

    @Nested
    class CommonTests {
        @Test
        void shouldNotAllowStatementWithLessThan10Characters() throws Exception {
            String request = String.format("""
                    {
                        "courseId": %d,
                        "statement": "Short",
                        "order": 1
                    }
                    """, course.getId());

            mockMvc.perform(post("/task/new/opentext")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(request))
                    .andExpect(status().isBadRequest())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.field").value("statement"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("O enunciado deve ter no mínimo 10 caracteres"));
        }

        @Test
        void shouldNotAllowDuplicateOrderInSameCourse() throws Exception {
            // Primeira tarefa com order = 1
            mockMvc.perform(post("/task/new/opentext")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(createOpenTextRequest(course.getId(), "What is Java Programming?", 1)))
                    .andExpect(status().isCreated());

            // Segunda tarefa tentando usar order = 1
            mockMvc.perform(post("/task/new/opentext")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(createOpenTextRequest(course.getId(), "What is Spring Framework?", 1)))
                    .andExpect(status().isBadRequest())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.field").value("order"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Já existe uma tarefa com esta ordem no curso"));
        }
    }

    @Nested
    class OpenTextTests {
        @Test
        void shouldNotAllowDuplicateStatementInSameCourse() throws Exception {
            String statement = "What is Java?";

            // Primeira requisição - deve passar
            mockMvc.perform(post("/task/new/opentext")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(createOpenTextRequest(course.getId(), statement, 1)))
                    .andExpect(status().isCreated());

            // Segunda requisição com mesmo enunciado - deve falhar
            mockMvc.perform(post("/task/new/opentext")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(createOpenTextRequest(course.getId(), statement, 2)))
                    .andExpect(status().isBadRequest())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.field").value("statement"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Já existe uma tarefa com este enunciado no curso"));
        }

        @Test
        public void shouldNotAllowNegativeOrder() throws Exception {
            String request = String.format("""
                    {
                        "courseId": %d,
                        "statement": "What is Java?",
                        "order": -1
                    }
                    """, course.getId());

            mockMvc.perform(post("/task/new/opentext")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(request))
                    .andExpect(status().isBadRequest())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.field").value("order"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("A ordem deve ser maior que zero"));
        }

        @Test
        public void shouldNotAllowZeroOrder() throws Exception {
            String request = String.format("""
                    {
                        "courseId": %d,
                        "statement": "What is Java?",
                        "order": 0
                    }
                    """, course.getId());

            mockMvc.perform(post("/task/new/opentext")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(request))
                    .andExpect(status().isBadRequest())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.field").value("order"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("A ordem deve ser maior que zero"));
        }

        @Test
        public void shouldNotAllowNullOrder() throws Exception {
            String request = String.format("""
                    {
                        "courseId": %d,
                        "statement": "What is Java?"
                    }
                    """, course.getId());

            mockMvc.perform(post("/task/new/opentext")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(request))
                    .andExpect(status().isBadRequest())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.field").value("order"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("A ordem é obrigatória"));
        }

        @Test
        public void shouldNotAllowTasksForNonBuildingCourse() throws Exception {
            // Criar um curso publicado para o teste
            User instructor = new User();
            instructor.setName("Another Instructor");
            instructor.setEmail("another@test.com");
            instructor.setPassword("123456");
            instructor.setRole(Role.INSTRUCTOR);
            instructor.isInstructor();
            entityManager.persist(instructor);

            Course course = new Course("Java Test Course", "Curso de teste para Java", instructor);
            course.setStatus(Status.PUBLISHED);
            courseRepository.save(course);

            String request = String.format("""
                    {
                        "courseId": %d,
                        "statement": "What is Java?",
                        "order": 1
                    }
                    """, course.getId());

            mockMvc.perform(post("/task/new/opentext")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(request))
                    .andExpect(status().isBadRequest())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.field").value("courseId"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("O curso precisa estar com status BUILDING para receber atividades"));
        }

        private String createOpenTextRequest(Long courseId, String statement, int order) {
            return String.format("""
                    {
                        "courseId": %d,
                        "statement": "%s",
                        "order": %d
                    }
                    """, courseId, statement, order);
        }
    }

    @Nested
    class SingleChoiceTests {
        private String createSingleChoiceRequest(Long courseId, String statement, int order, String options) {
            return String.format("""
                    {
                        "courseId": %d,
                        "statement": "%s",
                        "order": %d,
                        "options": %s
                    }
                    """, courseId, statement, order, options);
        }

        @Test
        void shouldNotAllowSingleChoiceWithoutOptions() throws Exception {
            mockMvc.perform(post("/task/new/singlechoice")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(createOpenTextRequest(course.getId(), "What is Java?", 1)))
                    .andExpect(status().isBadRequest())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.field").value("options"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("As opções são obrigatórias"));
        }

        @Test
        void shouldNotAllowEmptyOptionText() throws Exception {
            String request = createSingleChoiceRequest(course.getId(),
                "What is Java?",
                1,
                """
                [
                    {"option": "", "isCorrect": true},
                    {"option": "A type of coffee", "isCorrect": false}
                ]
                """);

            mockMvc.perform(post("/task/new/singlechoice")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(request))
                    .andExpect(status().isBadRequest())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.field").value("options[].option"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("O texto da opção não pode estar vazio"));
        }

        @Test
        void shouldNotAllowSingleChoiceWithoutCorrectOption() throws Exception {
            String request = String.format("""
                    {
                        "courseId": %d,
                        "statement": "What is Java?",
                        "order": 1,
                        "options": [
                            {"option": "A programming language", "isCorrect": false},
                            {"option": "A type of coffee", "isCorrect": false}
                        ]
                    }
                    """, course.getId());

            mockMvc.perform(post("/task/new/singlechoice")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(request))
                    .andExpect(status().isBadRequest())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.field").value("options"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("A questão deve ter exatamente uma alternativa correta"));
        }

        @Test
        void shouldNotAllowSingleChoiceWithMultipleCorrectOptions() throws Exception {
            String request = String.format("""
                    {
                        "courseId": %d,
                        "statement": "What is Java?",
                        "order": 1,
                        "options": [
                            {"option": "A programming language", "isCorrect": true},
                            {"option": "A type of coffee", "isCorrect": true}
                        ]
                    }
                    """, course.getId());

            mockMvc.perform(post("/task/new/singlechoice")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(request))
                    .andExpect(status().isBadRequest())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.field").value("options"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("A questão deve ter exatamente uma alternativa correta"));
        }

        @Test
        void shouldCreateSingleChoiceTaskSuccessfully() throws Exception {
            String request = String.format("""
                    {
                        "courseId": %d,
                        "statement": "What is Java?",
                        "order": 1,
                        "options": [
                            {"option": "A programming language", "isCorrect": true},
                            {"option": "A type of coffee", "isCorrect": false}
                        ]
                    }
                    """, course.getId());

            mockMvc.perform(post("/task/new/singlechoice")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(request))
                    .andExpect(status().isCreated())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("SINGLE_CHOICE"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.options").isArray())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.options.length()").value(2));
        }
    }

    @Nested
    class MultipleChoiceTests {
        private String createMultipleChoiceRequest(Long courseId, String statement, int order, String options) {
            return String.format("""
                    {
                        "courseId": %d,
                        "statement": "%s",
                        "order": %d,
                        "options": %s
                    }
                    """, courseId, statement, order, options);
        }

        @Test
        void shouldNotAllowMultipleChoiceWithoutOptions() throws Exception {
            mockMvc.perform(post("/task/new/multiplechoice")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(createOpenTextRequest(course.getId(), "Select the correct Java features:", 1)))
                    .andExpect(status().isBadRequest())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.field").value("options"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("As opções são obrigatórias"));
        }

        @Test
        void shouldNotAllowMultipleChoiceWithLessThanThreeOptions() throws Exception {
            String request = String.format("""
                    {
                        "courseId": %d,
                        "statement": "Select the correct Java features:",
                        "order": 1,
                        "options": [
                            {"option": "Object-oriented", "isCorrect": true},
                            {"option": "Procedural", "isCorrect": true}
                        ]
                    }
                    """, course.getId());

            mockMvc.perform(post("/task/new/multiplechoice")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(request))
                    .andExpect(status().isBadRequest())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.field").value("options"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("A questão deve ter no mínimo 3 alternativas"));
        }

        @Test
        void shouldNotAllowMultipleChoiceWithoutCorrectOptions() throws Exception {
            String request = String.format("""
                    {
                        "courseId": %d,
                        "statement": "Select the correct Java features:",
                        "order": 1,
                        "options": [
                            {"option": "Object-oriented", "isCorrect": false},
                            {"option": "Procedural", "isCorrect": false},
                            {"option": "Functional", "isCorrect": false}
                        ]
                    }
                    """, course.getId());

            mockMvc.perform(post("/task/new/multiplechoice")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(request))
                    .andExpect(status().isBadRequest())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.field").value("options"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("A questão deve ter pelo menos duas alternativas corretas"));
        }

        @Test
        void shouldNotAllowMultipleChoiceWithOnlyOneCorrectOption() throws Exception {
            String request = String.format("""
                    {
                        "courseId": %d,
                        "statement": "Select the correct Java features:",
                        "order": 1,
                        "options": [
                            {"option": "Object-oriented", "isCorrect": true},
                            {"option": "Procedural", "isCorrect": false},
                            {"option": "Functional", "isCorrect": false}
                        ]
                    }
                    """, course.getId());

            mockMvc.perform(post("/task/new/multiplechoice")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(request))
                    .andExpect(status().isBadRequest())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.field").value("options"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("A questão deve ter pelo menos duas alternativas corretas"));
        }

        @Test
        void shouldCreateMultipleChoiceTaskSuccessfully() throws Exception {
            String request = String.format("""
                    {
                        "courseId": %d,
                        "statement": "Select the correct Java features:",
                        "order": 1,
                        "options": [
                            {"option": "Object-oriented", "isCorrect": true},
                            {"option": "Platform independent", "isCorrect": true},
                            {"option": "Requires manual memory management", "isCorrect": false}
                        ]
                    }
                    """, course.getId());

            mockMvc.perform(post("/task/new/multiplechoice")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(request))
                    .andExpect(status().isCreated())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("MULTIPLE_CHOICE"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.options").isArray())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.options.length()").value(3));
        }
    }

    private String createOpenTextRequest(Long courseId, String statement, int order) {
        return String.format("""
                {
                    "courseId": %d,
                    "statement": "%s",
                    "order": %d
                }
                """, courseId, statement, order);
    }
}
