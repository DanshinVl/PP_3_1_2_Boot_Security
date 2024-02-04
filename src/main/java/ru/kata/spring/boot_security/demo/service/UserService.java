package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;
import java.util.Map;

public interface UserService {

    void delete(Long id);

    Map<User, String> getAllUsersWithRoles();

    String getUserRoles(User user);

    void addUserWithRoles(List<String> roles, User user);

    void updateUserWithRoles(User user, List<String> roles);

    User findById(Long id);

    User findByEmail(String email);
}
