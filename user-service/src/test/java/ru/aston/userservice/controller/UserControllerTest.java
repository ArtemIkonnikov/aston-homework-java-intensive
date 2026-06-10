package ru.aston.userservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.aston.userservice.dto.UserDto;
import ru.aston.userservice.service.UserService;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserControllerImpl.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @Test
    void getAll_returnsCollectionWithLinks() throws Exception {
        UserDto dto = new UserDto("Рик", "rick@example.com", 30);
        dto.setId(1L);
        when(userService.findAll()).thenReturn(List.of(dto));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded").exists())
                .andExpect(jsonPath("$._links.self.href").exists());
    }

    @Test
    void getById_existing_returns200WithSelfLink() throws Exception {
        UserDto dto = new UserDto("Морти", "morty@example.com", 25);
        dto.setId(2L);
        when(userService.findById(2L)).thenReturn(Optional.of(dto));

        mockMvc.perform(get("/users/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.name").value("Морти"))
                .andExpect(jsonPath("$._links.self.href").exists());
    }

    @Test
    void getById_missing_returns404() throws Exception {
        when(userService.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/users/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_valid_returns201() throws Exception {
        UserDto request = new UserDto("Рик", "rick@example.com", 30);
        UserDto saved = new UserDto("Рик", "rick@example.com", 30);
        saved.setId(1L);
        when(userService.create(any(UserDto.class))).thenReturn(saved);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Рик"))
                .andExpect(jsonPath("$._links.self.href").exists());
    }

    @Test
    void create_invalidEmail_returns400() throws Exception {
        UserDto request = new UserDto("Рик", "not-an-email", 30);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_blankName_returns400() throws Exception {
        UserDto request = new UserDto("", "rick@example.com", 30);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_existing_returns200() throws Exception {
        UserDto request = new UserDto("Рик Updated", "rick@example.com", 31);
        UserDto updated = new UserDto("Рик Updated", "rick@example.com", 31);
        updated.setId(1L);
        when(userService.update(eq(1L), any(UserDto.class))).thenReturn(Optional.of(updated));

        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Рик Updated"))
                .andExpect(jsonPath("$.age").value(31))
                .andExpect(jsonPath("$._links.self.href").exists());
    }

    @Test
    void update_missing_returns404() throws Exception {
        UserDto request = new UserDto("X", "x@example.com", 20);
        when(userService.update(eq(99L), any(UserDto.class))).thenReturn(Optional.empty());

        mockMvc.perform(put("/users/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_existing_returns204() throws Exception {
        when(userService.deleteById(1L)).thenReturn(true);

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_missing_returns404() throws Exception {
        when(userService.deleteById(99L)).thenReturn(false);

        mockMvc.perform(delete("/users/99"))
                .andExpect(status().isNotFound());
    }
}
