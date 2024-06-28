package com.qf.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qf.mapper.HfileMapper;
import com.qf.pojo.Hfile;
import com.qf.service.HfileService;
import org.springframework.stereotype.Service;

@Service
public class HfileServiceImpl extends ServiceImpl<HfileMapper, Hfile>implements HfileService {
}
