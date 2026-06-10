package ru.aston.userservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.aston.userservice.dto.UserDto;

@Tag(name = "Users", description = "API для управления пользователями")
@RequestMapping("/users")
public interface UserController {

    @Operation(summary = "Получить всех пользователей")
    @GetMapping
    ResponseEntity<CollectionModel<EntityModel<UserDto>>> getAll();

    @Operation(summary = "Получить пользователя по id")
    @GetMapping("/{id}")
    ResponseEntity<EntityModel<UserDto>> getById(@PathVariable("id") Long id);

    @Operation(summary = "Создать пользователя")
    @PostMapping
    ResponseEntity<EntityModel<UserDto>> create(@Valid @RequestBody UserDto user);

    @Operation(summary = "Обновить пользователя")
    @PutMapping("/{id}")
    ResponseEntity<EntityModel<UserDto>> update(@PathVariable("id") Long id, @Valid @RequestBody UserDto user);

    @Operation(summary = "Удалить пользователя")
    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable("id") Long id);
}
