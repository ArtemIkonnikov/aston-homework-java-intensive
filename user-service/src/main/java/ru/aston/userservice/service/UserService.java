package ru.aston.userservice.service;

import ru.aston.userservice.dto.UserDto;

import java.util.List;
import java.util.Optional;

public interface UserService {

    UserDto create(UserDto user);

    Optional<UserDto> findById(Long id);

    List<UserDto> findAll();

    Optional<UserDto> update(Long id, UserDto user);

    boolean deleteById(Long id);
}
