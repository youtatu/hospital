package com.qf.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qf.pojo.Doctor;

public interface DocService extends IService<Doctor> {
    boolean login(String username, String password);
}
