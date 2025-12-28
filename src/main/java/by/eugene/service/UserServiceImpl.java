package by.eugene.service;

import by.eugene.dao.UserDao;
import by.eugene.model.User;

import java.util.List;
import java.util.Optional;

public class UserServiceImpl implements UserService {

    private final UserDao userDao;

    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public User create(User user) {
        try {
            return userDao.save(user);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create user", e);
        }
    }

    @Override
    public Optional<User> getById(Long id) {
        return userDao.findById(id);
    }

    @Override
    public List<User> getAll() {
        return userDao.findAll();
    }

    @Override
    public User update(User user) {
        try {
            return userDao.update(user);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update user", e);
        }
    }

    @Override
    public boolean delete(Long id) {
        try {
            return userDao.delete(id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete user", e);
        }
    }
}
