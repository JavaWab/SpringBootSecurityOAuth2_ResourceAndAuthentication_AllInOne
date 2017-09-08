package com.dyrs.api.dao;

import com.dyrs.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author wanganbang
 */
@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    User findByUsername(String name);

}
