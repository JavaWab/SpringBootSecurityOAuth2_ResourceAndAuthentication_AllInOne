package com.dyrs.api.dao;

import com.dyrs.api.entity.IMMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MessageDAO extends JpaRepository<IMMessage, String> {
    @Query(nativeQuery = true, value = "select * from t_message where (from_uid=?1 and to_uid=?2 ) or (to_uid=?1 and from_uid=?2) order by time desc limit ?3,?4")
    List<IMMessage> findAllByFromUIDaAndToUID(String from_uid, String to_uid, int start, int pageSize);

    @Query(nativeQuery = true, value = "select COUNT(msg_id) from t_message where ((from_uid=?1 and to_uid=?2 ) or (to_uid=?1 and from_uid=?2)) and time > ?3")
    int countAllByromUIDaAndToUID(String from_uid, String to_uid, java.sql.Date time);
}
