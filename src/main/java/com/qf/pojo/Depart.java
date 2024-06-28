package com.qf.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("depart")
public class Depart {
    @TableId
    private Integer id;
    private String departName;
    private String descr;
}
