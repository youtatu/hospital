package com.qf.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.qf.pojo.Notice;
import com.qf.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("notice")
public class NoticeController {
    @Autowired
    private NoticeService noticeService;

    @RequestMapping("listNotice")
    public String listNotice(@RequestParam(required = false,value = "pageNum",defaultValue = "1")Integer pageNum,
                             @RequestParam(required = false,value = "pageSize",defaultValue = "10")Integer pageSize, Model model, Notice notice){
        if (pageNum==null || pageNum<=0 || pageNum.equals("")){
            pageNum=1;
        }
        if (pageSize==null || pageSize<=0 || pageSize.equals("")){
            pageSize=10;
        }
        PageHelper.startPage(pageNum,pageSize);
        QueryWrapper<Notice>qw=new QueryWrapper<>();
        if (notice.getNname()!=null){
            qw.like("nname",notice.getNname());
        }
        List<Notice> list = noticeService.list(qw);
        PageInfo<Notice>pageInfo=new PageInfo<>(list);
        model.addAttribute("pageInfo",pageInfo);
        return "admin-notice-list";
    }

    @RequestMapping("preSaveNotice")
    public String preSaveNotice(){
        return "admin-notice-save";
    }

    @RequestMapping("saveNotice")
    public String saveNotice(Notice notice){
        boolean save = noticeService.save(notice);
        return "redirect:/notice/listNotice";
    }

    @RequestMapping("delNotice/{id}")
    public String delNotice(@PathVariable Integer id){
        boolean b = noticeService.removeById(id);
        return  "redirect:/notice/listNotice";
    }


    @RequestMapping("userListNotice")
    public String userListNotice(@RequestParam(required = false,value = "pageNum",defaultValue = "1")Integer pageNum,
                             @RequestParam(required = false,value = "pageSize",defaultValue = "10")Integer pageSize, Model model, Notice notice){
        if (pageNum==null || pageNum<=0 || pageNum.equals("")){
            pageNum=1;
        }
        if (pageSize==null || pageSize<=0 || pageSize.equals("")){
            pageSize=10;
        }
        PageHelper.startPage(pageNum,pageSize);
        QueryWrapper<Notice>qw=new QueryWrapper<>();
        if (notice.getNname()!=null){
            qw.like("nname",notice.getNname());
        }
        List<Notice> list = noticeService.list(qw);
        PageInfo<Notice>pageInfo=new PageInfo<>(list);
        model.addAttribute("pageInfo",pageInfo);
        return "user-notice-list";
    }
}
