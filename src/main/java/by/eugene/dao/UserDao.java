package by.eugene.dao;



import by.eugene.model.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {
    User save(User user) throws Exception;
    Optional<User> findById(Long id);
    List<User> findAll();
    User update(User user) throws Exception;
    boolean delete(Long id) throws Exception;
}
