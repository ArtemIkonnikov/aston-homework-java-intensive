package ru.aston.userservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.aston.userservice.dto.UserDto;
import ru.aston.userservice.entity.User;
import ru.aston.userservice.kafka.UserEventProducer;
import ru.aston.userservice.mapper.UserMapper;
import ru.aston.userservice.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final UserEventProducer userEventProducer;

    public UserServiceImpl(UserRepository userRepository, UserEventProducer userEventProducer) {
        this.userRepository = userRepository;
        this.userEventProducer = userEventProducer;
    }

    @Override
    public UserDto create(UserDto dto) {
        log.info("Creating user: name={}, email={}", dto.getName(), dto.getEmail());
        User user = UserMapper.toEntity(dto);
        User saved = userRepository.save(user);
        userEventProducer.send("CREATE", saved.getEmail());
        return UserMapper.toDto(saved);
    }

    @Override
    public Optional<UserDto> findById(Long id) {
        log.info("Looking up user by id={}", id);
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            return Optional.of(UserMapper.toDto(user.get()));
        }
        return Optional.empty();
    }

    @Override
    public List<UserDto> findAll() {
        log.info("Listing all users");
        List<User> users = userRepository.findAll();
        List<UserDto> result = new ArrayList<>();
        for (User user : users) {
            result.add(UserMapper.toDto(user));
        }
        return result;
    }

    @Override
    public Optional<UserDto> update(Long id, UserDto dto) {
        log.info("Updating user id={}", id);
        Optional<User> existing = userRepository.findById(id);
        if (existing.isEmpty()) {
            return Optional.empty();
        }
        User user = existing.get();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setAge(dto.getAge());
        User saved = userRepository.save(user);
        return Optional.of(UserMapper.toDto(saved));
    }

    @Override
    public boolean deleteById(Long id) {
        log.info("Deleting user id={}", id);
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            return false;
        }
        String email = user.get().getEmail();
        userRepository.deleteById(id);
        userEventProducer.send("DELETE", email);
        return true;
    }
}
