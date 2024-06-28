package com.qf.service.impl;

import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qf.mapper.PatMapper;
import com.qf.pojo.Patient;
import com.qf.service.PatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PatServiceImpl extends ServiceImpl<PatMapper, Patient>implements PatService {
    @Autowired
    private PatMapper patMapper;
    @Override
    public boolean login(String username, String password) {

        QueryWrapper<Patient> qw=new QueryWrapper<>();
        qw.eq("pname",username);
        Patient user = patMapper.selectOne(qw);
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
