package com.qf.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qf.mapper.HosMapper;
import com.qf.pojo.Hospital;
import com.qf.service.HosService;
import org.springframework.stereotype.Service;

@Service
public class HosServiceImpl extends ServiceImpl<HosMapper, Hospital> implements HosService {

}
