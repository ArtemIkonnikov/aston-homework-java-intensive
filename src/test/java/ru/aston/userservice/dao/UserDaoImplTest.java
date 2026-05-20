package ru.aston.userservice.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.aston.userservice.dto.UserDto;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
class UserDaoImplTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("test_db")
            .withUsername("test")
            .withPassword("test");

    private static SessionFactory sessionFactory;
    private UserDaoImpl userDao;

    @BeforeAll
    static void initFactory() {
        sessionFactory = new Configuration()
                .configure("hibernate.cfg.xml")
                .setProperty("hibernate.connection.url", postgres.getJdbcUrl())
                .setProperty("hibernate.connection.username", postgres.getUsername())
                .setProperty("hibernate.connection.password", postgres.getPassword())
                .setProperty("hibernate.hbm2ddl.auto", "create-drop")
                .buildSessionFactory();
    }

    @AfterAll
    static void closeFactory() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    @BeforeEach
    void cleanTable() {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.createNativeMutationQuery("TRUNCATE TABLE users RESTART IDENTITY").executeUpdate();
            tx.commit();
        }
        userDao = new UserDaoImpl(sessionFactory);
    }

    @Test
    void save_validUser_assignsIdAndCreatedAt() {
        UserDto dto = new UserDto("Rick", "Rick@example.com", 30);

        UserDto saved = userDao.save(dto);

        assertNotNull(saved.getId());
        assertNotNull(saved.getCreatedAt());
        assertEquals("Rick", saved.getName());
        assertEquals("Rick@example.com", saved.getEmail());
        assertEquals(30, saved.getAge());
    }

    @Test
    void save_duplicateEmail_throwsException() {
        userDao.save(new UserDto("Rick", "duplicate@example.com", 30));

        UserDto another = new UserDto("Morty", "duplicate@example.com", 25);

        assertThrows(RuntimeException.class, () -> userDao.save(another));
    }

    @Test
    void findById_existingId_returnsUser() {
        UserDto saved = userDao.save(new UserDto("Morty", "Morty@example.com", 25));

        Optional<UserDto> found = userDao.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals("Morty", found.get().getName());
        assertEquals("Morty@example.com", found.get().getEmail());
    }

    @Test
    void findById_nonExistingId_returnsEmpty() {
        Optional<UserDto> found = userDao.findById(9999L);

        assertTrue(found.isEmpty());
    }

    @Test
    void findAll_emptyDb_returnsEmptyList() {
        List<UserDto> all = userDao.findAll();

        assertTrue(all.isEmpty());
    }

    @Test
    void findAll_multipleUsers_returnsAllOrderedById() {
        userDao.save(new UserDto("Rick", "Rick@example.com", 30));
        userDao.save(new UserDto("Morty", "Morty@example.com", 25));
        userDao.save(new UserDto("BoJack", "BoJack@example.com", 40));

        List<UserDto> all = userDao.findAll();

        assertEquals(3, all.size());
        assertEquals("Rick", all.get(0).getName());
        assertEquals("Morty", all.get(1).getName());
        assertEquals("BoJack", all.get(2).getName());
    }

    @Test
    void update_existingUser_changesFields() {
        UserDto saved = userDao.save(new UserDto("Rick", "Rick@example.com", 30));
        saved.setName("Rick Updated");
        saved.setAge(31);

        userDao.update(saved);

        Optional<UserDto> reloaded = userDao.findById(saved.getId());
        assertTrue(reloaded.isPresent());
        assertEquals("Rick Updated", reloaded.get().getName());
        assertEquals(31, reloaded.get().getAge());
    }

    @Test
    void deleteById_existingId_returnsTrueAndRemoves() {
        UserDto saved = userDao.save(new UserDto("Rick", "Rick@example.com", 30));

        boolean deleted = userDao.deleteById(saved.getId());

        assertTrue(deleted);
        assertTrue(userDao.findById(saved.getId()).isEmpty());
    }

    @Test
    void deleteById_nonExistingId_returnsFalse() {
        boolean deleted = userDao.deleteById(9999L);

        assertFalse(deleted);
    }
}
