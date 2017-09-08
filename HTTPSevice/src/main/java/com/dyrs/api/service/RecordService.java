package com.dyrs.api.service;

import com.dyrs.api.entity.vo.LeavingMessageVO;

public interface RecordService {
    Long addRecord(LeavingMessageVO messageVO);
}
