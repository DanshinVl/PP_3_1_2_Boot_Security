package ru.kata.spring.boot_security.demo.service;

import org.springframework.data.domain.Sort;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.dto.RoleDto;
import ru.kata.spring.boot_security.demo.dto.UserDto;
import ru.kata.spring.boot_security.demo.dto.UserMapper;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import ru.kata.spring.boot_security.demo.repository.UserRepository;
import ru.kata.spring.boot_security.demo.model.Role;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository,
                            RoleRepository roleRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userMapper = userMapper;
    }

    @Transactional
    @Override
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public Map<User, List<String>> getAllUsersWithRoles() {
        return userRepository
                .findAll(Sort.by("id"))
                .stream()
                .sorted(Comparator.comparing(User::getId))
                .collect(Collectors.toMap(
                        Function.identity(),
                        user -> user.getRoles().stream()
                                .map(Role::getName)
                                .map(roleName -> roleName.replace("ROLE_", ""))
                                .sorted()
                                .toList(),
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new
                ));
    }

    @Transactional(readOnly = true)
    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException(email);
        }
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
        return new org.springframework.security.core.userdetails.
                User(user.getUsername(), user.getPassword(), authorities);
    }

    @Transactional
    @Override
    public void addUserWithRoles(UserDto userDto) {
        User user = userMapper.toModel(userDto);

        setRolesToUser(user, userDto.getRoles());

        userRepository.save(user);
    }

    private void setRolesToUser(User user, Set<RoleDto> roleDtoSet) {
        Set<Role> roles = roleDtoSet.stream()
                .map(roleDto -> roleRepository.findByName("ROLE_" + roleDto.getName()))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        user.setRoles(roles);
    }

    @Transactional
    @Override
    public void updateUserWithRoles(UserDto userDto) {
        User existingUser = userRepository.findByEmail(userDto.getEmail());

        if (existingUser != null) {
            existingUser.setFirstname(userDto.getFirstname());
            existingUser.setLastname(userDto.getLastname());
            existingUser.setAge(userDto.getAge());
            existingUser.setEmail(userDto.getEmail());
            existingUser.setPassword(userDto.getPassword());
            setRolesToUser(existingUser, userDto.getRoles());

            userRepository.save(existingUser);
        }
    }
}
