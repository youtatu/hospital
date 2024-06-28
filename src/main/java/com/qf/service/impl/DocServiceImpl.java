package com.qf.service.impl;

import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qf.mapper.DocMapper;
import com.qf.pojo.Doctor;
import com.qf.service.DocService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DocServiceImpl extends ServiceImpl<DocMapper, Doctor>implements DocService {
    @Autowired
    private DocMapper docMapper;
    @Override
    public boolean login(String username, String password) {

        QueryWrapper<Doctor> qw=new QueryWrapper<>();
        qw.eq("dname",username);
        Doctor user = docMapper.selectOne(qw);
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
