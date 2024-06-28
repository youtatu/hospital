package com.qf.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("hfile")
public class Hfile {

    private  Integer id;
    private String fname;
    private String fimage;
    private String hfile;
    private  Integer role;
}
