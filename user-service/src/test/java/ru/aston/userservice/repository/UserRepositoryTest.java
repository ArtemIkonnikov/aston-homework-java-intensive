package ru.aston.userservice.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.aston.userservice.entity.User;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class UserRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private UserRepository userRepository;

    @Test
    void save_assignsIdAndCreatedAt() {
        User user = new User();
        user.setName("Рик");
        user.setEmail("rick@example.com");
        user.setAge(30);

        User saved = userRepository.save(user);

        assertNotNull(saved.getId());
        assertNotNull(saved.getCreatedAt());
        assertEquals("Рик", saved.getName());
    }

    @Test
    void findById_existing_returnsUser() {
        User user = new User();
        user.setName("Морти");
        user.setEmail("morty@example.com");
        user.setAge(25);
        User saved = userRepository.save(user);

        Optional<User> found = userRepository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals("Морти", found.get().getName());
    }

    @Test
    void findAll_returnsAllSavedUsers() {
        User a = new User();
        a.setName("Рик");
        a.setEmail("rick@example.com");
        a.setAge(30);
        User b = new User();
        b.setName("Морти");
        b.setEmail("morty@example.com");
        b.setAge(25);
        userRepository.save(a);
        userRepository.save(b);

        List<User> all = userRepository.findAll();

        assertEquals(2, all.size());
    }

    @Test
    void deleteById_removesUser() {
        User user = new User();
        user.setName("Рик");
        user.setEmail("rick@example.com");
        user.setAge(30);
        User saved = userRepository.save(user);

        userRepository.deleteById(saved.getId());

        assertTrue(userRepository.findById(saved.getId()).isEmpty());
    }
}
