package com.hotel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hotel.entity.Message;

import java.util.List;

public interface MessageService extends IService<Message> {
    List<Message> getAllMessages();

    boolean readMessage(Integer id);
}