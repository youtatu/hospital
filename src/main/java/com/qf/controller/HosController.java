package com.qf.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.qf.pojo.Hospital;
import com.qf.service.HosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("hos")
public class HosController {

    @Value("${location}")
    private String location;
    @Autowired
    private HosService hosService;

    //查询所有医院
    @RequestMapping("listHos")
    public String listHos(@RequestParam(required = false,value = "pageNum",defaultValue = "1")Integer pageNum,
                          @RequestParam(required = false,value = "pageSize",defaultValue = "10")Integer pageSize, Model model, Hospital hospital){
        //分页，一页存10个数据
        if (pageNum==null || pageNum<=0 || pageNum.equals("")){
            pageNum=1;
        }
        if (pageSize==null || pageSize<=0 || pageSize.equals("")){
            pageSize=10;
        }

        PageHelper.startPage(pageNum,pageSize);
        //创建了一个查询对象，操作数据库通过这个对象操作
        QueryWrapper<Hospital>qw=new QueryWrapper<>();
        if (hospital.getHname()!=null){
            //MySql的模糊查询（Mybatis简化了代码）
            qw.like("hname",hospital.getHname());
        }
        //用来查询医院的信息：通过Service层调用Mapper层，取查询数据库的医院信息
        List<Hospital> list = hosService.list(qw);
        //分页
        PageInfo<Hospital>pageInfo=new PageInfo<>(list);
        //医院的信息存到model对象里面，然后返回给前端
        model.addAttribute("pageInfo",pageInfo);
        //跳转到医院列表界面
        return "admin-hos-list";

    }

    @RequestMapping("preSaveHos")
    public String preSaveHos(){
        return "admin-hos-save";
    }

    @RequestMapping("saveHos")
    public String saveHos(Hospital hospital, MultipartFile file){
        //上传文件
        transfile(hospital,file);
        //通过Service层调用Mapper层，将医院的信息添加到数据库中。
        boolean save = hosService.save(hospital);
        return "redirect:/hos/listHos";

    }

    private void transfile(Hospital hospital, MultipartFile file) {//保存头像
        String originalFilename = file.getOriginalFilename();

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
            file.transferTo(file2);
        } catch (IOException e) {
            e.printStackTrace();
        }

        hospital.setHimage(path);
    }


    @RequestMapping("preUpdateHos/{id}")
    public String preUpdateHos(@PathVariable Integer id,Model model){
        //通过id 查到医院
        Hospital hospital = hosService.getById(id);
        //医院的信息存到model对象里面，然后返回给前端
        model.addAttribute("hospital",hospital);
        return "admin-hos-update";
    }

    @RequestMapping("updateHos")
    public String updateHos(Hospital hospital){
        //前端返回数据（hospital对象）给Controller层，Controller层调用Service层，Service层调用Mapper，Mapper去操作数据库，将医院的信息存储到数据库中
        boolean b = hosService.updateById(hospital);
        return "redirect:/hos/listHos";
    }

    @RequestMapping("delHos/{id}")
    public String delHos(@PathVariable Integer id){
        boolean b = hosService.removeById(id);
        return "redirect:/hos/listHos";
    }

    @RequestMapping("batchDeleteHos")
    @ResponseBody
    public String batchDeleteHos(String idList){
        String[] split = StrUtil.split(idList, ",");
        List<Integer>list=new ArrayList<>();
        for (String s : split) {
            if (!s.isEmpty()){
                Integer integer = Integer.valueOf(s);
                list.add(integer);
            }
        }

        boolean b = hosService.removeByIds(list);
        if (b){
            return "OK";
        }else {
            return "error";
        }
    }
}
