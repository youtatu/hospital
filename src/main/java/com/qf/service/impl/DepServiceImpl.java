package com.qf.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qf.mapper.DepMapper;
import com.qf.pojo.Depart;
import com.qf.service.DepService;
import org.springframework.stereotype.Service;

@Service
public class DepServiceImpl extends ServiceImpl<DepMapper, Depart>implements DepService {
}
