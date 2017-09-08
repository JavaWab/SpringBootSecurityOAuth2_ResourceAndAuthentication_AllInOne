package com.dyrs.api.service;

import com.dyrs.api.entity.User;

import java.util.List;

/**
 * @author wanganbang
 */
public interface UserService {

    User findByUsername(String name);

    List<User> findAll();

    void saveUser(User user);

    boolean isUserExist(User user);
}
