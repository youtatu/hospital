package com.qf.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qf.pojo.User;

public interface UserService extends IService<User> {
    boolean login(String username, String password);
}
