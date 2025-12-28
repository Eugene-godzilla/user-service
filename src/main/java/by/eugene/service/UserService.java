package by.eugene.service;


import by.eugene.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    User create(User user);

    Optional<User> getById(Long id);

    List<User> getAll();

    User update(User user);

    boolean delete(Long id);
}
