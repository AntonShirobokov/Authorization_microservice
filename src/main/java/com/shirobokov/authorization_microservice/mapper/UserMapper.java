package com.shirobokov.authorization_microservice.mapper;

import com.shirobokov.authorization_microservice.dto.UserLoginDTO;
import com.shirobokov.authorization_microservice.dto.UserRegistrationDTO;
import com.shirobokov.authorization_microservice.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserRegistrationDTO toUserRegistrationDTO(User user);

    User toUser(UserRegistrationDTO userRegistrationDTO);

    UserLoginDTO toUserLoginDTO (User user);

    User toUser(UserLoginDTO userLoginDTO);
}
