package ru.kata.spring.boot_security.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.dto.UserDto;
import ru.kata.spring.boot_security.demo.dto.UserMapper;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserService;


import java.security.Principal;
import java.util.*;


@Controller
public class UserAdminController {
    private final UserService userService;

    private final UserMapper userMapper;

    public UserAdminController(UserService userService,
                               UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @GetMapping(value = "/admin")
    public String printUsersAndCurrentUser(Model model, Principal principal) {
        User currentUser = userService.findByEmail(principal.getName());
        String userRoles = userService.getUserRoles(currentUser);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("userRoles", userRoles);

        Map<User, String> usersWithRoles = userService.getAllUsersWithRoles();
        model.addAttribute("usersWithRoles", usersWithRoles);

        return "admin";
    }

    @PostMapping(value = "/admin/add")
    public String addUser(@ModelAttribute UserDto userDto, @RequestParam List<String> role) {
        User user = userMapper.toModel(userDto);
        userService.addUserWithRoles(role, user);

        return "redirect:/admin";
    }

    @PostMapping(value = "/admin/update")
    public String updateUser(@ModelAttribute UserDto userDto,
                             @RequestParam(required = false) List<String> role) {
        User user = userMapper.toModel(userDto);
        userService.updateUserWithRoles(user, role);

        return "redirect:/admin";
    }

    @PostMapping(value = "/admin/delete")
    public String deleteUser(@RequestParam Long id) {
        userService.delete(id);

        return "redirect:/admin";
    }

}
