package com.qf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qf.pojo.CountNumber;
import com.qf.pojo.Visit;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VisitMapper extends BaseMapper<Visit> {
    List<CountNumber> queryNum();

    List<Visit> listMyVisit(Integer userId);

    List<Visit> listDocVisit(Integer userId);
}
