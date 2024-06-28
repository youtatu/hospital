package com.qf.pojo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@TableName("visit")
public class Visit {
    @TableId
    private Integer id;
    private Integer pid;
    private Integer did;
    @TableField(fill = FieldFill.INSERT) //插入时填充字段
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date registerTime;
    private Integer status;

    @TableField(exist = false)
    private  Patient patient;
    @TableField(exist = false)
    private Doctor doctor;
    private String pfile;
    private String dfile;
}
