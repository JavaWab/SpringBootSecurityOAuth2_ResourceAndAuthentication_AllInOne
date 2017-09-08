package com.dyrs.api.service;

import com.dyrs.api.entity.IMMessage;

import java.util.List;

/**
 *
 */
public interface MessageService {
    List<IMMessage> getMessageHistory(int page, String from, String to);

    void persistentMessages();

    int get7MessagesCount(String from, String to);
}
