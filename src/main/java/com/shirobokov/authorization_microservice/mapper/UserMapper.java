package com.shirobokov.authorization_microservice.mapper;

import com.shirobokov.authorization_microservice.dto.UserLoginDTO;
import com.shirobokov.authorization_microservice.dto.UserRegistrationDTO;
import com.shirobokov.authorization_microservice.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "password", target="passwordHash")
    User toUser(UserRegistrationDTO userRegistrationDTO);

    @Mapping(source = "password", target="passwordHash")
    User toUser(UserLoginDTO userLoginDTO);
}
