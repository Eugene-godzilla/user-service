package by.eugene.service;

import by.eugene.dao.UserDao;
import by.eugene.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserDao userDao;
    private UserService userService;

    @BeforeEach
    void setup() {
        userDao = mock(UserDao.class);
        userService = new UserServiceImpl(userDao);
    }

    @Test
    void createUser_success() throws Exception {
        User user = User.builder().name("mock").email("mock@mail.com").build();

        when(userDao.save(user)).thenReturn(user);

        User result = userService.create(user);

        assertEquals("mock", result.getName());
        verify(userDao, times(1)).save(user);
    }

    @Test
    void createUser_exceptionFromDao() throws Exception {
        User user = User.builder().name("x").email("x@mail.com").build();

        when(userDao.save(user)).thenThrow(new Exception("DB down"));

        assertThrows(RuntimeException.class, () -> userService.create(user));
    }

    @Test
    void getById_existing() {
        User user = User.builder().name("user").email("u@mail.com").build();
        when(userDao.findById(1L)).thenReturn(Optional.of(user));

        Optional<User> result = userService.getById(1L);

        assertTrue(result.isPresent());
        assertEquals("user", result.get().getName());
    }

    @Test
    void getById_notFound() {
        when(userDao.findById(1L)).thenReturn(Optional.empty());

        Optional<User> result = userService.getById(1L);

        assertTrue(result.isEmpty());
    }

    @Test
    void getAll_returnsList() {
        List<User> users = List.of(
                User.builder().name("A").email("a@mail.com").build(),
                User.builder().name("B").email("b@mail.com").build()
        );
        when(userDao.findAll()).thenReturn(users);

        List<User> result = userService.getAll();

        assertEquals(2, result.size());
    }

    @Test
    void updateUser_success() throws Exception {
        User user = User.builder().name("Old").email("old@mail.com").build();
        when(userDao.update(user)).thenReturn(user);

        User updated = userService.update(user);

        assertEquals("Old", updated.getName());
        verify(userDao).update(user);
    }

    @Test
    void updateUser_exceptionFromDao() throws Exception {
        User user = User.builder().name("X").email("x@mail.com").build();
        when(userDao.update(user)).thenThrow(new Exception("DB error"));

        assertThrows(RuntimeException.class, () -> userService.update(user));
    }

    @Test
    void deleteUser_existing() throws Exception {
        when(userDao.delete(1L)).thenReturn(true);

        boolean deleted = userService.delete(1L);

        assertTrue(deleted);
        verify(userDao).delete(1L);
    }

    @Test
    void deleteUser_notExisting() throws Exception {
        when(userDao.delete(999L)).thenReturn(false);

        boolean deleted = userService.delete(999L);

        assertFalse(deleted);
        verify(userDao).delete(999L);
    }
}
