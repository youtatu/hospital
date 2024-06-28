package com.qf.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.qf.pojo.Hfile;
import com.qf.service.HfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("file")
public class HfileController {

    @Value("${location}")
    private String location;
    @Value("${filelocation}")
    private String filelocation;
    @Autowired
    private HfileService hfileService;

    @RequestMapping("listFile")
    public String listFile(@RequestParam(required = false,value = "pageNum",defaultValue = "1")Integer pageNum,
                           @RequestParam(required = false,value = "pageSize",defaultValue = "10")Integer pageSize, Model model, Hfile hfile){
        if (pageNum==null || pageNum<=0 || pageNum.equals("")){
            pageNum=1;
        }
        if (pageSize==null || pageSize<=0 || pageSize.equals("")){
            pageSize=10;
        }
        PageHelper.startPage(pageNum,pageSize);

        QueryWrapper<Hfile>qw=new QueryWrapper<>();
        if (hfile.getFname()!=null){
            qw.like("fname",hfile.getFname());
        }
        List<Hfile> list = hfileService.list(qw);
        PageInfo<Hfile>pageInfo=new PageInfo<>(list);
        model.addAttribute("pageInfo",pageInfo);
        return "admin-file-list";
    }

    @RequestMapping("preSaveFile")
    public String preSaveFile(){
        return "admin-file-save";
    }

    @RequestMapping("saveFile")
    public String saveFile(Hfile hfile, MultipartFile filea,MultipartFile fileb){
        transfilea(hfile,filea);
        transfileb(hfile,fileb);
        boolean save = hfileService.save(hfile);
        return "redirect:/file/listFile";
    }

    private void transfileb(Hfile hfile, MultipartFile fileb) {
        String originalFilename = fileb.getOriginalFilename();

        String path=originalFilename;
        File file1 = new File(filelocation);
        if (!file1.exists()){
            file1.mkdirs();
        }
        File file2 = new File(file1, path);
        try {
            fileb.transferTo(file2);
        } catch (IOException e) {
            e.printStackTrace();
        }
        hfile.setHfile(path);
    }


    private void transfilea(Hfile hfile, MultipartFile filea) {
        String originalFilename = filea.getOriginalFilename();
        int index = originalFilename.lastIndexOf(".");
        String suffix = originalFilename.substring(index);
        String prefix=System.nanoTime()+"";
        String path=prefix+suffix;
        File file1 = new File(location);
        if (!file1.exists()){
            file1.mkdirs();
        }
        File file2 = new File(file1, path);
        try {
            filea.transferTo(file2);
        } catch (IOException e) {
            e.printStackTrace();
        }
        hfile.setFimage(path);
    }

    @RequestMapping("delFile/{id}")
    public String delFile(@PathVariable Integer id){
        boolean b = hfileService.removeById(id);
        return "redirect:/file/listFile";
    }

}
