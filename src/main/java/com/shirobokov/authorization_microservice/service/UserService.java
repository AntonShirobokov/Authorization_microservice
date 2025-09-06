package com.shirobokov.authorization_microservice.service;


import com.shirobokov.authorization_microservice.entity.Role;
import com.shirobokov.authorization_microservice.entity.User;
import com.shirobokov.authorization_microservice.exception.UserAlreadyExistsException;
import com.shirobokov.authorization_microservice.mapper.UserMapper;
import com.shirobokov.authorization_microservice.repository.RoleRepository;
import com.shirobokov.authorization_microservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    public User findUserByEmail(String email) throws Exception {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw new Exception("Пользователь не найден");
        }
        return user.get();
    }

    public User findUserByUserId(UUID uuid) throws Exception {
        Optional<User> user = userRepository.findByUserId(uuid);
        if (user.isEmpty()) {
            throw new Exception("Пользователь не найден");
        }
        return user.get();
    }

    @Transactional
    public void save(User user) throws UserAlreadyExistsException {
        // Проверка, существует ли пользователь с таким email
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new UserAlreadyExistsException("Пользователь с таким email уже зарегистрирован");
        }

        // Получаем существующую роль ROLE_USER или создаем новую, если её нет
        Role role = roleRepository.findByName("ROLE_USER").get();

        // Устанавливаем роль и дату создания
        user.setRoles(Collections.singletonList(role));
        user.setCreatedAt(LocalDateTime.now());

        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));

        // Сохраняем пользователя
        userRepository.save(user);
    }


}
