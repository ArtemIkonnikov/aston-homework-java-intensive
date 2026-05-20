package ru.aston.userservice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.aston.userservice.dao.UserDao;
import ru.aston.userservice.dto.UserDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserDao userDao;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void create_validInput_callsDaoSaveAndReturnsResult() {
        UserDto input = new UserDto("Rick", "Rick@example.com", 30);
        UserDto saved = new UserDto("Rick", "Rick@example.com", 30);
        saved.setId(1L);
        when(userDao.save(input)).thenReturn(saved);

        UserDto result = userService.create(input);

        assertSame(saved, result);
        verify(userDao, times(1)).save(input);
    }

    @Test
    void findById_existingId_callsDaoAndReturnsResult() {
        UserDto dto = new UserDto("Morty", "Morty@example.com", 25);
        dto.setId(2L);
        when(userDao.findById(2L)).thenReturn(Optional.of(dto));

        Optional<UserDto> result = userService.findById(2L);

        assertTrue(result.isPresent());
        assertSame(dto, result.get());
        verify(userDao, times(1)).findById(2L);
    }

    @Test
    void findById_nonExistingId_returnsEmpty() {
        when(userDao.findById(99L)).thenReturn(Optional.empty());

        Optional<UserDto> result = userService.findById(99L);

        assertTrue(result.isEmpty());
        verify(userDao, times(1)).findById(99L);
    }

    @Test
    void findAll_callsDaoAndReturnsList() {
        UserDto a = new UserDto("Rick", "Rick@example.com", 30);
        UserDto b = new UserDto("Morty", "Morty@example.com", 25);
        List<UserDto> expected = List.of(a, b);
        when(userDao.findAll()).thenReturn(expected);

        List<UserDto> result = userService.findAll();

        assertEquals(2, result.size());
        assertSame(expected, result);
        verify(userDao, times(1)).findAll();
    }

    @Test
    void findAll_emptyDb_returnsEmptyList() {
        when(userDao.findAll()).thenReturn(new ArrayList<>());

        List<UserDto> result = userService.findAll();

        assertTrue(result.isEmpty());
        verify(userDao, times(1)).findAll();
    }

    @Test
    void update_validInput_callsDaoUpdate() {
        UserDto dto = new UserDto("Rick Updated", "Rick@example.com", 31);
        dto.setId(1L);

        userService.update(dto);

        verify(userDao, times(1)).update(dto);
    }

    @Test
    void deleteById_existingId_returnsTrue() {
        when(userDao.deleteById(5L)).thenReturn(true);

        boolean result = userService.deleteById(5L);

        assertTrue(result);
        verify(userDao, times(1)).deleteById(5L);
    }

    @Test
    void deleteById_nonExistingId_returnsFalse() {
        when(userDao.deleteById(99L)).thenReturn(false);

        boolean result = userService.deleteById(99L);

        assertFalse(result);
        verify(userDao, times(1)).deleteById(99L);
    }

    @Test
    void create_threeUsers_callsDaoSaveThreeTimes() {
        UserDto savedStub = new UserDto("X", "x@x.x", 1);
        savedStub.setId(1L);
        when(userDao.save(org.mockito.ArgumentMatchers.any(UserDto.class))).thenReturn(savedStub);

        userService.create(new UserDto("A", "a@a.a", 1));
        userService.create(new UserDto("B", "b@b.b", 2));
        userService.create(new UserDto("C", "c@c.c", 3));

        verify(userDao, times(3)).save(org.mockito.ArgumentMatchers.any(UserDto.class));
    }

    @Test
    void noServiceCall_noInteractionWithDao() {
        verifyNoInteractions(userDao);
    }
}
