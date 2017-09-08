package com.dyrs.api.dao;

import com.dyrs.api.entity.Record;
import org.springframework.data.repository.Repository;

public interface RecordDAO extends Repository<Record, Long> {
    Record save(Record record);

    Record findById(Long id);
}
