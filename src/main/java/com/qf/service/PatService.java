package com.qf.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qf.pojo.Patient;

public interface PatService extends IService<Patient> {
    boolean login(String username, String password);
}
