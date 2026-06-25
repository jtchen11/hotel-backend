package com.hotel.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hotel.entity.GroupInfo;
import com.hotel.mapper.GroupInfoMapper;
import com.hotel.service.GroupInfoService;
import org.springframework.stereotype.Service;

@Service
public class GroupInfoServiceImpl extends ServiceImpl<GroupInfoMapper, GroupInfo> implements GroupInfoService {
}