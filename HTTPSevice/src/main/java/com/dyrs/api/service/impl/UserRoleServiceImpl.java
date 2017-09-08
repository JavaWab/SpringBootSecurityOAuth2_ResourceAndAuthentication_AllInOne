package com.dyrs.api.service.impl;

import com.dyrs.api.dao.UserRoleRepository;
import com.dyrs.api.service.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author wanganbang
 */
@Service
public class UserRoleServiceImpl implements UserRoleService {

    @Autowired
    private UserRoleRepository userRoleRepository;
    @Override
    public List<String> findRoles(int uid) {

        List<String> list = userRoleRepository.findRoleName(uid);

        return list;
    }
}
