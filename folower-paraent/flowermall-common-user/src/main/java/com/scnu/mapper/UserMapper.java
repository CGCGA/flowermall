package com.scnu.mapper;

import com.scnu.pojo.User;

public interface UserMapper {
    Integer queryUserName(String userName);

    void userSave(User user);

    User queryUserByNameAndPassword(User user);
}
