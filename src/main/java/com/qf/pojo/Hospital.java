package com.qf.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("hospital")
public class Hospital {
    @TableId
    private Integer id;
    private String hname;
    private String descr;
    private String himage;
    private String address;

    private String phone;
}
