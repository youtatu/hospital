package com.qf.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("doctor")
public class Doctor {
    @TableId
    private Integer id;
    private String dname;
    private String password;
    private String sex;
    private Integer age;
    private String phone;
    private String dimage;
    private String depart;
    private Integer hid;
    @TableField(exist = false)
    private String hospital;
}
