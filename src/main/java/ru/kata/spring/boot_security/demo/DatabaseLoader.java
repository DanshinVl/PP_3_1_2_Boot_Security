package ru.kata.spring.boot_security.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import ru.kata.spring.boot_security.demo.repository.UserRepository;


import java.util.HashSet;
import java.util.List;

@Component
public class DatabaseLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public DatabaseLoader(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... strings) {
        Role adminRole = new Role("ROLE_ADMIN");
        Role userRole = new Role("ROLE_USER");

        this.roleRepository.save(adminRole);
        this.roleRepository.save(userRole);

        User admin = new User("admin", "admin@mail.ru", "admin");
        admin.setRoles(new HashSet<>(List.of(adminRole, userRole)));

        User user = new User("user", "user@mail.ru", "user");
        user.setRoles(new HashSet<>(List.of(userRole)));

        this.userRepository.save(admin);
        this.userRepository.save(user);
    }
}