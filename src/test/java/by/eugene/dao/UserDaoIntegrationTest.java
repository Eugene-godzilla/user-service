package by.eugene.dao;

import by.eugene.model.User;
import by.eugene.util.HibernateUtil;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class UserDaoIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:15-alpine")
                    .withDatabaseName("testdb")
                    .withUsername("test")
                    .withPassword("test");

    private static UserDao userDao;

    @BeforeAll
    static void setup() {
        postgres.start();

        System.setProperty("db.url", postgres.getJdbcUrl());
        System.setProperty("db.username", postgres.getUsername());
        System.setProperty("db.password", postgres.getPassword());

        userDao = new UserDaoImpl();
    }

    @BeforeEach
    void cleanDb() {
        try (var session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.createQuery("DELETE FROM User").executeUpdate();
            session.getTransaction().commit();
        }
    }

    @Test
    void saveUser() throws Exception {
        User user = User.builder()
                .name("test")
                .email("test@test.com")
                .age(20)
                .build();

        User saved = userDao.save(user);

        assertNotNull(saved.getId());
    }

    @Test
    void findById_existingUser() throws Exception {
        User user = userDao.save(User.builder()
                .name("find")
                .email("find@test.com")
                .build());

        Optional<User> found = userDao.findById(user.getId());

        assertTrue(found.isPresent());
        assertEquals("find", found.get().getName());
    }

    @Test
    void findById_notExisting() {
        Optional<User> found = userDao.findById(999L);
        assertTrue(found.isEmpty());
    }

    @Test
    void findAll_returnsAllUsers() throws Exception {
        userDao.save(User.builder().name("A").email("a@test.com").build());
        userDao.save(User.builder().name("B").email("b@test.com").build());

        List<User> all = userDao.findAll();

        assertEquals(2, all.size());
    }

    @Test
    void updateUser_success() throws Exception {
        User user = userDao.save(User.builder().name("Old").email("old@test.com").build());
        user.setName("New");
        user.setAge(30);

        User updated = userDao.update(user);
        Optional<User> fetched = userDao.findById(user.getId());

        assertEquals("New", updated.getName());
        assertEquals(30, fetched.get().getAge());
    }

    @Test
    void deleteUser_existing() throws Exception {
        User user = userDao.save(User.builder().name("delete").email("delete@test.com").build());

        boolean deleted = userDao.delete(user.getId());

        assertTrue(deleted);
        assertTrue(userDao.findById(user.getId()).isEmpty());
    }

    @Test
    void deleteUser_notExisting() throws Exception {
        boolean deleted = userDao.delete(999L);
        assertFalse(deleted);
    }

    @AfterAll
    static void tearDown() {
        HibernateUtil.shutdown();
    }
}
