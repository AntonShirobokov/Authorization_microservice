package com.shirobokov.authorization_microservice.mapper;

import com.shirobokov.authorization_microservice.dto.UserDTO;
import com.shirobokov.authorization_microservice.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO toUserDTO(User user);

    User toUser(UserDTO userDTO);
}
