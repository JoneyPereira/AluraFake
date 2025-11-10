package br.com.alura.AluraFake.instructor;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.CourseRepository;
import br.com.alura.AluraFake.course.Status;
import br.com.alura.AluraFake.task.TaskRepository;
import br.com.alura.AluraFake.user.Role;
import br.com.alura.AluraFake.user.User;
import br.com.alura.AluraFake.user.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class InstructorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private EntityManager entityManager;

    private User instructor;
    private User student;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
        courseRepository.deleteAll();
        userRepository.deleteAll();

        instructor = createUser("Maria", "maria@email.com", Role.INSTRUCTOR);
        student = createUser("João", "joao@email.com", Role.STUDENT);
    }

    private User createUser(String name, String email, Role role) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword("123456");
        user.setRole(role);
        return userRepository.save(user);
    }

    private Course createCourse(String title, User instructor, String discription, Status status) {
        Course course = new Course();
        course.setTitle(title);
        course.setInstructor(instructor);
        course.setDescription(discription);
        course.setStatus(status);
        if (status == Status.PUBLISHED) {
            course.setPublishedAt(LocalDateTime.now());
        }
        return courseRepository.save(course);
    }

    @Test
    void shouldReturn404WhenUserDoesNotExist() throws Exception {
        mockMvc.perform(get("/instructor/999/courses"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.field").value("id"))
                .andExpect(jsonPath("$.message").value("Usuário não encontrado"));
    }

    @Test
    void shouldReturn400WhenUserIsNotInstructor() throws Exception {
        mockMvc.perform(get("/instructor/" + student.getId() + "/courses"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.field").value("role"))
                .andExpect(jsonPath("$.message").value("Usuário não é um instrutor"));
    }

    @Test
    void shouldReturnCoursesWhenUserIsInstructor() throws Exception {
        Course course1 = createCourse("Java Básico",instructor, "Java", Status.PUBLISHED);
        Course course2 = createCourse("Spring Boot", instructor, "Spring", Status.BUILDING);

        mockMvc.perform(get("/instructor/" + instructor.getId() + "/courses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courses").isArray())
                .andExpect(jsonPath("$.courses.length()").value(2))
                .andExpect(jsonPath("$.courses[0].id").value(course1.getId()))
                .andExpect(jsonPath("$.courses[0].title").value("Java Básico"))
                .andExpect(jsonPath("$.courses[0].status").value("PUBLISHED"))
                .andExpect(jsonPath("$.courses[0].publishedAt").isNotEmpty())
                .andExpect(jsonPath("$.courses[1].id").value(course2.getId()))
                .andExpect(jsonPath("$.courses[1].title").value("Spring Boot"))
                .andExpect(jsonPath("$.courses[1].status").value("BUILDING"))
                .andExpect(jsonPath("$.totalPublishedCourses").value(1));
    }

    @Test
    void shouldReturnEmptyListWhenInstructorHasNoCourses() throws Exception {
        mockMvc.perform(get("/instructor/" + instructor.getId() + "/courses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courses").isArray())
                .andExpect(jsonPath("$.courses.length()").value(0))
                .andExpect(jsonPath("$.totalPublishedCourses").value(0));
    }
}
