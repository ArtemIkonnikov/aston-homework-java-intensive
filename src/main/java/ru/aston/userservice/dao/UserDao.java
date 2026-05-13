package ru.aston.userservice.dao;

import ru.aston.userservice.dto.UserDto;

import java.util.List;
import java.util.Optional;

public interface UserDao {

    UserDto save(UserDto user);

    Optional<UserDto> findById(Long id);

    List<UserDto> findAll();

    void update(UserDto user);

    boolean deleteById(Long id);
}
