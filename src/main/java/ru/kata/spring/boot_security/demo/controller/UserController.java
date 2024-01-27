package ru.kata.spring.boot_security.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.security.Principal;

/**
 * Контроллер для задач, связанных с пользователями.
 * Доступен для ролей ROLE_USER и ROLE_ADMIN
 */
@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Отображает страницу пользователя с его данными.
     * @param model MVC модель
     * @param principal абстракция пользователя
     * @return userPage
     */
    @GetMapping("/user")
    public String getUserPage(Model model, Principal principal) {
        String username = principal.getName();
        User user = userService.findByUsername(username);
        model.addAttribute("user", user);
        return "user";
    }

    /**
     * Отображает страницу пользователя по идентификатору.
     * Доступен только для ROLE_ADMIN
     * @param id идентификатор пользователя
     * @param model MVC модель
     * @return userPage
     */
    @GetMapping("/user/{id}")
    public String getUserPageById(@PathVariable Long id, Model model) {
        User user = userService.findById(id);
        model.addAttribute("user", user);
        return "user";
    }

}
