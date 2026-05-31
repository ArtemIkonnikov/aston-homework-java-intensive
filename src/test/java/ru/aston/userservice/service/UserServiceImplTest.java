package ru.aston.userservice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.aston.userservice.dto.UserDto;
import ru.aston.userservice.entity.User;
import ru.aston.userservice.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void create_savesAndReturnsDto() {
        UserDto input = new UserDto("Рик", "rick@example.com", 30);
        User savedEntity = new User();
        savedEntity.setId(1L);
        savedEntity.setName("Рик");
        savedEntity.setEmail("rick@example.com");
        savedEntity.setAge(30);
        when(userRepository.save(any(User.class))).thenReturn(savedEntity);

        UserDto result = userService.create(input);

        assertEquals(1L, result.getId());
        assertEquals("Рик", result.getName());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void findById_existing_returnsDto() {
        User entity = new User();
        entity.setId(2L);
        entity.setName("Морти");
        entity.setEmail("morty@example.com");
        entity.setAge(25);
        when(userRepository.findById(2L)).thenReturn(Optional.of(entity));

        Optional<UserDto> result = userService.findById(2L);

        assertTrue(result.isPresent());
        assertEquals("Морти", result.get().getName());
        verify(userRepository, times(1)).findById(2L);
    }

    @Test
    void findById_missing_returnsEmpty() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<UserDto> result = userService.findById(99L);

        assertTrue(result.isEmpty());
        verify(userRepository, times(1)).findById(99L);
    }

    @Test
    void findAll_returnsList() {
        User a = new User();
        a.setId(1L);
        a.setName("Рик");
        a.setEmail("rick@example.com");
        a.setAge(30);
        User b = new User();
        b.setId(2L);
        b.setName("Морти");
        b.setEmail("morty@example.com");
        b.setAge(25);
        when(userRepository.findAll()).thenReturn(List.of(a, b));

        List<UserDto> result = userService.findAll();

        assertEquals(2, result.size());
        assertEquals("Рик", result.get(0).getName());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void update_existing_updatesFields() {
        User entity = new User();
        entity.setId(1L);
        entity.setName("Рик");
        entity.setEmail("rick@example.com");
        entity.setAge(30);
        when(userRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(userRepository.save(any(User.class))).thenReturn(entity);

        UserDto changes = new UserDto("Рик Updated", "rick@example.com", 31);
        Optional<UserDto> result = userService.update(1L, changes);

        assertTrue(result.isPresent());
        assertEquals("Рик Updated", result.get().getName());
        assertEquals(31, result.get().getAge());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void update_missing_returnsEmpty() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<UserDto> result = userService.update(99L, new UserDto("X", "x@example.com", 20));

        assertTrue(result.isEmpty());
        verify(userRepository, times(1)).findById(99L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deleteById_existing_returnsTrue() {
        when(userRepository.existsById(5L)).thenReturn(true);

        boolean result = userService.deleteById(5L);

        assertTrue(result);
        verify(userRepository, times(1)).deleteById(5L);
    }

    @Test
    void deleteById_missing_returnsFalse() {
        when(userRepository.existsById(99L)).thenReturn(false);

        boolean result = userService.deleteById(99L);

        assertFalse(result);
        verify(userRepository, never()).deleteById(anyLong());
    }
}
