package by.eugene.dao;

import by.eugene.model.User;
import by.eugene.util.HibernateUtil;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class UserDaoImpl implements UserDao {
    private static final Logger logger = LoggerFactory.getLogger(UserDaoImpl.class);

    @Override
    public User save(User user) throws Exception {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Long id = (Long) session.save(user);
            tx.commit();
            user.setId(id);
            logger.info("Saved user with id {}", id);
            return user;
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            logger.error("Error saving user", e);
            throw new Exception("DB error: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<User> findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            User user = session.get(User.class, id);
            return Optional.ofNullable(user);
        }
    }

    @Override
    public List<User> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from User", User.class).list();
        }
    }

    @Override
    public User update(User user) throws Exception {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.update(user);
            tx.commit();
            logger.info("Updated user id {}", user.getId());
            return user;
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            logger.error("Error updating user", e);
            throw new Exception("DB error: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean delete(Long id) throws Exception {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            User user = session.get(User.class, id);
            if (user == null) {
                tx.rollback();
                return false;
            }
            session.delete(user);
            tx.commit();
            logger.info("Deleted user id {}", id);
            return true;
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            logger.error("Error deleting user", e);
            throw new Exception("DB error: " + e.getMessage(), e);
        }
    }
}