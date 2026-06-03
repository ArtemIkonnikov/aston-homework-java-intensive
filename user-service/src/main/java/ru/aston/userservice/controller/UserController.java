package ru.aston.userservice.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.aston.userservice.dto.UserDto;

import java.util.List;

@RequestMapping("/users")
public interface UserController {

    @GetMapping
    ResponseEntity<List<UserDto>> getAll();

    @GetMapping("/{id}")
    ResponseEntity<UserDto> getById(@PathVariable("id") Long id);

    @PostMapping
    ResponseEntity<UserDto> create(@Valid @RequestBody UserDto user);

    @PutMapping("/{id}")
    ResponseEntity<UserDto> update(@PathVariable("id") Long id, @Valid @RequestBody UserDto user);

    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable("id") Long id);
}
