package ru.kata.spring.boot_security.demo.service;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.kata.spring.boot_security.demo.model.Role;

@Component
public interface RoleService {

    Role findByName(String name);
}
