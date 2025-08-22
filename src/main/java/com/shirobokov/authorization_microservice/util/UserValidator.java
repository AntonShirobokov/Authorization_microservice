package com.shirobokov.authorization_microservice.util;


import com.shirobokov.authorization_microservice.entity.User;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.regex.Pattern;

@Component
public class UserValidator implements Validator {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^[\\p{ASCII}]{10,}$");

    @Override
    public boolean supports(Class<?> clazz) {
        return User.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        User user = (User) target;

        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            errors.rejectValue("email", "email.empty", "Email не может быть пустым");
        } else if (!EMAIL_PATTERN.matcher(user.getEmail()).matches()) {
            errors.rejectValue("email", "email.invalid", "Некорректный формат email");
        } else if (containsCyrillic(user.getEmail())) {
            errors.rejectValue("email", "email.cyrillic", "Email должен содержать только латинские символы");
        }

        if (user.getPasswordHash() == null || user.getPasswordHash().trim().isEmpty()) {
            errors.rejectValue("passwordHash", "password.empty", "Пароль не может быть пустым");
        } else if (user.getPasswordHash().length() < 10) {
            errors.rejectValue("passwordHash", "password.short", "Пароль должен содержать минимум 10 символов");
        } else if (!PASSWORD_PATTERN.matcher(user.getPasswordHash()).matches()) {
            errors.rejectValue("passwordHash", "password.invalid", "Пароль должен содержать только латинские символы и цифры");
        }
    }


    private boolean containsCyrillic(String value) {
        return value != null && value.matches(".*[\\p{IsCyrillic}].*");
    }
}
