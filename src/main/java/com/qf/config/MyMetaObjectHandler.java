package com.qf.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    //这段代码是一个方法，目的是向metaObject对象中插入字段值。
    // 具体来说，它将当前日期作为"registerTime"字段的值插入到metaObject对象中。
    @Override
    public void insertFill(MetaObject metaObject) {
        this.setFieldValByName("registerTime",new Date(),metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {

    }
}

