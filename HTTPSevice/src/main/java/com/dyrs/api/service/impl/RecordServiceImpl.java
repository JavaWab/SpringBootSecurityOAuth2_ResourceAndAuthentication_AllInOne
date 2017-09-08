package com.dyrs.api.service.impl;

import com.dyrs.api.dao.RecordDAO;
import com.dyrs.api.entity.Record;
import com.dyrs.api.entity.vo.LeavingMessageVO;
import com.dyrs.api.service.RecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;

@Service
public class RecordServiceImpl implements RecordService {
    @Autowired
    private RecordDAO recordDAO;

    @Override
    public Long addRecord(LeavingMessageVO messageVO) {
        Record record = new Record();
        record.setContent(messageVO.getContent());
        record.setDate(new Date(System.currentTimeMillis()));
        record.setName(messageVO.getName());
        record.setPhone(messageVO.getPhone());
        record.setJiaJuID(messageVO.getJiaJuId());
        record = recordDAO.save(record);
        return record.getId();
    }
}
