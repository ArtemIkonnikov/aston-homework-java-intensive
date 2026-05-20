package ru.aston.userservice.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.aston.userservice.dto.UserDto;
import ru.aston.userservice.entity.User;
import ru.aston.userservice.mapper.UserMapper;
import ru.aston.userservice.util.HibernateUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDaoImpl implements UserDao {

    private static final Logger log = LoggerFactory.getLogger(UserDaoImpl.class);

    private final SessionFactory sessionFactory;

    public UserDaoImpl() {
        this(HibernateUtil.getSessionFactory());
    }

    public UserDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public UserDto save(UserDto dto) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            User user = UserMapper.toEntity(dto);
            session.persist(user);
            transaction.commit();
            log.info("User saved with id={}", user.getId());
            return UserMapper.toDto(user);
        } catch (ConstraintViolationException e) {
            rollback(transaction);
            log.error("Constraint violation while saving user: {}", e.getMessage());
            throw new RuntimeException("Could not save user (email may already exist)", e);
        } catch (Exception e) {
            rollback(transaction);
            log.error("Error while saving user: {}", e.getMessage(), e);
            throw new RuntimeException("Could not save user: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<UserDto> findById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            User user = session.get(User.class, id);
            return Optional.ofNullable(UserMapper.toDto(user));
        } catch (Exception e) {
            log.error("Error while looking up user by id={}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Could not find user by id=" + id, e);
        }
    }

    @Override
    public List<UserDto> findAll() {
        try (Session session = sessionFactory.openSession()) {
            List<User> users = session
                    .createQuery("FROM User ORDER BY id", User.class)
                    .getResultList();
            List<UserDto> dtos = new ArrayList<>();
            for (User user : users) {
                dtos.add(UserMapper.toDto(user));
            }
            log.info("Loaded {} users", dtos.size());
            return dtos;
        } catch (Exception e) {
            log.error("Error while loading users: {}", e.getMessage(), e);
            throw new RuntimeException("Could not load users", e);
        }
    }

    @Override
    public void update(UserDto dto) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            User user = UserMapper.toEntity(dto);
            session.merge(user);
            transaction.commit();
            log.info("User updated, id={}", dto.getId());
        } catch (ConstraintViolationException e) {
            rollback(transaction);
            log.error("Constraint violation while updating user: {}", e.getMessage());
            throw new RuntimeException("Could not update user (email may already exist)", e);
        } catch (Exception e) {
            rollback(transaction);
            log.error("Error while updating user: {}", e.getMessage(), e);
            throw new RuntimeException("Could not update user: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deleteById(Long id) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            User user = session.get(User.class, id);
            if (user == null) {
                transaction.rollback();
                return false;
            }
            session.remove(user);
            transaction.commit();
            log.info("User deleted, id={}", id);
            return true;
        } catch (Exception e) {
            rollback(transaction);
            log.error("Error while deleting user id={}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Could not delete user: " + e.getMessage(), e);
        }
    }

    private void rollback(Transaction transaction) {
        if (transaction != null && transaction.isActive()) {
            try {
                transaction.rollback();
            } catch (Exception rollbackEx) {
                log.error("Rollback failed: {}", rollbackEx.getMessage(), rollbackEx);
            }
        }
    }
}
