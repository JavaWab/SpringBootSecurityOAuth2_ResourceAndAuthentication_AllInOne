package com.dyrs.api.service.impl;

import com.dyrs.api.dao.UserRepository;
import com.dyrs.api.entity.Role;
import com.dyrs.api.entity.User;
import com.dyrs.api.service.SystemService;
import com.dyrs.api.utils.Blowfish;
import com.sun.crypto.provider.BlowfishCipher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: Wang Anbang
 * Time: 17-9-7 下午5:56
 */
@Service
public class SystemServiceImpl implements SystemService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public void init() throws Exception {
        Role userRole = new Role();
        userRole.setRole_name("ROLE_USER");
        Role adminRole = new Role();
        adminRole.setRole_name("ROLE_ADMIN");
        Md5PasswordEncoder md5 = new Md5PasswordEncoder();

        ArrayList<Role> roles = new ArrayList<Role>();
        roles.add(userRole);
        roles.add(adminRole);
        User user = new User();
        user.setUsername("admin");
        user.setPassword(md5.encodePassword("admin", null));
        user.setList(roles);
        userRepository.save(user);
    }
}
