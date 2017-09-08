package com.dyrs.api.dao;

import com.dyrs.api.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author wanganbang
 */
@Repository
public interface RoleRepository extends JpaRepository<Role,Long> {
}
