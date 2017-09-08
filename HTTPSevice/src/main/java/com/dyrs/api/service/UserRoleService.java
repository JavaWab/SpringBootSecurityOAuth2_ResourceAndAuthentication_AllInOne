package com.dyrs.api.service;

import java.util.List;

/**
 * @author wanganbang
 */
public interface UserRoleService {

    List<String> findRoles(int uid);
}
