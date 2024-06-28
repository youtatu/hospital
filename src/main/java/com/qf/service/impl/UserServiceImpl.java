package com.qf.service.impl;

import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qf.mapper.UserMapper;
import com.qf.pojo.User;
import com.qf.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Override
    public boolean login(String username, String password) {

        QueryWrapper<User>qw=new QueryWrapper<>();
        qw.eq("username",username);
        User user = userMapper.selectOne(qw);
        if (user==null){
            return false;
        }

        String s = DigestUtil.md5Hex(password);
        if (s.equals(user.getPassword())){
            return true;
        }else {
            return false;
        }
    }
}
