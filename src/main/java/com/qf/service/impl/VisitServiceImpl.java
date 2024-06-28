package com.qf.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qf.mapper.VisitMapper;
import com.qf.pojo.CountNumber;
import com.qf.pojo.Visit;
import com.qf.service.VisitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VisitServiceImpl extends ServiceImpl<VisitMapper, Visit>implements VisitService {
    @Autowired
    private VisitMapper visitMapper;
    @Override
    public List<CountNumber> queryNum() {
        return visitMapper.queryNum();
    }

    @Override
    public List<Visit> listMyVisit(Integer userId) {
        return visitMapper.listMyVisit(userId) ;
    }

    @Override
    public List<Visit> listDocVisit(Integer userId) {
        return visitMapper.listDocVisit(userId);
    }
}
