package com.qf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qf.pojo.Patient;
import org.springframework.stereotype.Repository;

@Repository
public interface PatMapper extends BaseMapper<Patient> {
}
