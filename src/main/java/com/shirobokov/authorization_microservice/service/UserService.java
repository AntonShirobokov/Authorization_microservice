package com.shirobokov.authorization_microservice.service;


import com.shirobokov.authorization_microservice.entity.Role;
import com.shirobokov.authorization_microservice.entity.User;
import com.shirobokov.authorization_microservice.exception.UserAlreadyExistsException;
import com.shirobokov.authorization_microservice.mapper.UserMapper;
import com.shirobokov.authorization_microservice.repository.RoleRepository;
import com.shirobokov.authorization_microservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

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

        // Сохраняем пользователя
        userRepository.save(user);
    }
}
