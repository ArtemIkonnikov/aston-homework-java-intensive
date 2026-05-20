package ru.aston.userservice.console;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.aston.userservice.dto.UserDto;
import ru.aston.userservice.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class ConsoleMenu {

    private static final Logger log = LoggerFactory.getLogger(ConsoleMenu.class);

    private final UserService userService;
    private final Scanner scanner;

    public ConsoleMenu(UserService userService) {
        this.userService = userService;
        this.scanner = new Scanner(System.in);
    }

    public void run() {
        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();
            try {
                switch (choice) {
                    case "1" -> createUser();
                    case "2" -> findUserById();
                    case "3" -> findAllUsers();
                    case "4" -> updateUser();
                    case "5" -> deleteUser();
                    case "0" -> {
                        running = false;
                        System.out.println("Bye!");
                    }
                    default -> System.out.println("Unknown option, please try again");
                }
            } catch (RuntimeException e) {
                System.out.println("Operation failed: " + e.getMessage());
                log.warn("Operation failed for choice '{}'", choice, e);
            }
        }
    }

    private void printMenu() {
        System.out.println();
        System.out.println("==== User Service ====");
        System.out.println("1. Create user");
        System.out.println("2. Find user by id");
        System.out.println("3. Show all users");
        System.out.println("4. Update user");
        System.out.println("5. Delete user");
        System.out.println("0. Exit");
        System.out.print("Choose action: ");
    }

    private void createUser() {
        System.out.print("Enter name: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("Name cannot be empty");
            return;
        }

        System.out.print("Enter email: ");
        String email = scanner.nextLine().trim();
        if (email.isEmpty()) {
            System.out.println("Email cannot be empty");
            return;
        }

        System.out.print("Enter age: ");
        String ageInput = scanner.nextLine().trim();
        if (ageInput.isEmpty()) {
            System.out.println("Age cannot be empty");
            return;
        }
        int age;
        try {
            age = Integer.parseInt(ageInput);
        } catch (NumberFormatException e) {
            System.out.println("Invalid age");
            return;
        }

        UserDto user = new UserDto(name, email, age);
        UserDto saved = userService.create(user);
        System.out.println("Created: " + saved);
    }

    private void findUserById() {
        System.out.print("Enter id: ");
        Long id = parseLong(scanner.nextLine().trim());
        if (id == null) {
            System.out.println("Invalid id");
            return;
        }
        Optional<UserDto> user = userService.findById(id);
        if (user.isPresent()) {
            System.out.println(user.get());
        } else {
            System.out.println("User with id=" + id + " not found");
        }
    }

    private void findAllUsers() {
        List<UserDto> users = userService.findAll();
        if (users.isEmpty()) {
            System.out.println("No users found");
            return;
        }
        System.out.println("Total users: " + users.size());
        for (UserDto user : users) {
            System.out.println(user);
        }
    }

    private void updateUser() {
        System.out.print("Enter id of user to update: ");
        Long id = parseLong(scanner.nextLine().trim());
        if (id == null) {
            System.out.println("Invalid id");
            return;
        }

        Optional<UserDto> existing = userService.findById(id);
        if (existing.isEmpty()) {
            System.out.println("User with id=" + id + " not found");
            return;
        }

        UserDto user = existing.get();
        System.out.println("Current: " + user);

        System.out.print("New name (press Enter to keep current): ");
        String name = scanner.nextLine();
        if (!name.isBlank()) {
            user.setName(name.trim());
        }

        System.out.print("New email (press Enter to keep current): ");
        String email = scanner.nextLine();
        if (!email.isBlank()) {
            user.setEmail(email.trim());
        }

        System.out.print("New age (press Enter to keep current): ");
        String ageInput = scanner.nextLine().trim();
        if (!ageInput.isEmpty()) {
            try {
                user.setAge(Integer.parseInt(ageInput));
            } catch (NumberFormatException e) {
                System.out.println("Invalid age, keeping current");
            }
        }

        userService.update(user);
        System.out.println("Updated: " + user);
    }

    private void deleteUser() {
        System.out.print("Enter id of user to delete: ");
        Long id = parseLong(scanner.nextLine().trim());
        if (id == null) {
            System.out.println("Invalid id");
            return;
        }
        boolean deleted = userService.deleteById(id);
        if (deleted) {
            System.out.println("User with id=" + id + " deleted");
        } else {
            System.out.println("User with id=" + id + " not found");
        }
    }

    private Long parseLong(String s) {
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

}
