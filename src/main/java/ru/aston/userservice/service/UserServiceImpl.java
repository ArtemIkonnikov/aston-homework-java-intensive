package ru.aston.userservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.aston.userservice.dao.UserDao;
import ru.aston.userservice.dto.UserDto;

import java.util.List;
import java.util.Optional;

public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserDao userDao;

    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public UserDto create(UserDto user) {
        log.info("Creating user: name={}, email={}", user.getName(), user.getEmail());
        return userDao.save(user);
    }

    @Override
    public Optional<UserDto> findById(Long id) {
        log.info("Looking up user by id={}", id);
        return userDao.findById(id);
    }

    @Override
    public List<UserDto> findAll() {
        log.info("Listing all users");
        return userDao.findAll();
    }

    @Override
    public void update(UserDto user) {
        log.info("Updating user id={}", user.getId());
        userDao.update(user);
    }

    @Override
    public boolean deleteById(Long id) {
        log.info("Deleting user id={}", id);
        return userDao.deleteById(id);
    }
}
