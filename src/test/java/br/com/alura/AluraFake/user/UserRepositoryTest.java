package br.com.alura.AluraFake.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ActiveProfiles("test")
@Transactional
@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void cleanUp() {
        userRepository.deleteAll();
    }

    @Test
    void findByEmail__should_return_existis_user() {
        User caio = new User("Joney", "joney@alura.com.br", Role.STUDENT);
        userRepository.save(caio);

        Optional<User> result = userRepository.findByEmail("joney@alura.com.br");
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Joney");

        result = userRepository.findByEmail("sergio@alura.com.br");
        assertThat(result).isEmpty();
    }

    @Test
    void existsByEmail__should_return_true_when_user_existis() {
        User caio = new User("Joney", "joney@alura.com.br", Role.STUDENT);
        userRepository.save(caio);

        assertThat(userRepository.existsByEmail("joney@alura.com.br")).isTrue();
        assertThat(userRepository.existsByEmail("sergio@alura.com.br")).isFalse();
    }
}