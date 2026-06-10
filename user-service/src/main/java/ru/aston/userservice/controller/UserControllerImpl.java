package ru.aston.userservice.controller;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.aston.userservice.dto.UserDto;
import ru.aston.userservice.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class UserControllerImpl implements UserController {

    private final UserService userService;

    public UserControllerImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public ResponseEntity<CollectionModel<EntityModel<UserDto>>> getAll() {
        List<UserDto> users = userService.findAll();
        List<EntityModel<UserDto>> models = new ArrayList<>();
        for (UserDto user : users) {
            EntityModel<UserDto> model = EntityModel.of(user);
            model.add(linkTo(methodOn(UserController.class).getById(user.getId())).withSelfRel());
            models.add(model);
        }
        CollectionModel<EntityModel<UserDto>> collection = CollectionModel.of(models);
        collection.add(linkTo(methodOn(UserController.class).getAll()).withSelfRel());
        return ResponseEntity.ok(collection);
    }

    @Override
    public ResponseEntity<EntityModel<UserDto>> getById(Long id) {
        Optional<UserDto> user = userService.findById(id);
        if (user.isPresent()) {
            EntityModel<UserDto> model = EntityModel.of(user.get());
            model.add(linkTo(methodOn(UserController.class).getById(id)).withSelfRel());
            model.add(linkTo(methodOn(UserController.class).getAll()).withRel("users"));
            return ResponseEntity.ok(model);
        }
        return ResponseEntity.notFound().build();
    }

    @Override
    public ResponseEntity<EntityModel<UserDto>> create(UserDto user) {
        UserDto created = userService.create(user);
        EntityModel<UserDto> model = EntityModel.of(created);
        model.add(linkTo(methodOn(UserController.class).getById(created.getId())).withSelfRel());
        model.add(linkTo(methodOn(UserController.class).getAll()).withRel("users"));
        return ResponseEntity.status(HttpStatus.CREATED).body(model);
    }

    @Override
    public ResponseEntity<EntityModel<UserDto>> update(Long id, UserDto user) {
        Optional<UserDto> updated = userService.update(id, user);
        if (updated.isPresent()) {
            EntityModel<UserDto> model = EntityModel.of(updated.get());
            model.add(linkTo(methodOn(UserController.class).getById(id)).withSelfRel());
            model.add(linkTo(methodOn(UserController.class).getAll()).withRel("users"));
            return ResponseEntity.ok(model);
        }
        return ResponseEntity.notFound().build();
    }

    @Override
    public ResponseEntity<Void> delete(Long id) {
        boolean deleted = userService.deleteById(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
