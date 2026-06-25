package com.hotel.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hotel.entity.Message;
import com.hotel.mapper.MessageMapper;
import com.hotel.service.MessageService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {
    @Override
    public List<Message> getAllMessages() {
        return baseMapper.selectMessageWithRoom();
    }
    @Override
    public boolean readMessage(Integer id) {
        Message message = baseMapper.selectById(id);
        if (message == null) return false;
        message.setIsRead(1);
        message.setReadTime(LocalDateTime.now());
        return updateById(message);
    }
}