package com.qf.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qf.pojo.CountNumber;
import com.qf.pojo.Visit;

import java.util.List;

public interface VisitService extends IService<Visit> {
    List<CountNumber> queryNum();

    List<Visit> listMyVisit(Integer userId);

    List<Visit> listDocVisit(Integer userId);
}
