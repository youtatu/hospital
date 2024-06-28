package com.qf.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("patient")
public class Patient {
    @TableId
    private Integer id;
    private String pname;
    private String password;
    private String sex;
    private Integer age;
    private String address;
    private String phone;
    private String pimage;
}
