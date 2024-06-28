package com.qf.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.qf.pojo.Depart;
import com.qf.service.DepService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("dep")
public class DepController {
    @Autowired
    private DepService depService;



    @RequestMapping("listDep")
    //这段代码完成了部门列表的功能。
    // 它首先接收参数pageNum和pageSize，用于分页显示部门列表。
    // 然后使用PageHelper进行分页设置，利用QueryWrapper对部门名称进行模糊查询过滤。
    // 最后将查询结果封装成PageInfo对象，然后将PageInfo对象传递给前端视图模型，展示部门列表。
    public String listDep(@RequestParam(required = false,value = "pageNum",defaultValue = "1")Integer pageNum,
                          @RequestParam(required = false,value = "pageSize",defaultValue = "10")Integer pageSize, Model model, Depart depart){
        if (pageNum==null || pageNum<=0 || pageNum.equals("")){
            pageNum=1;
        }
        if (pageSize==null || pageSize<=0 || pageSize.equals("")){
            pageSize=10;
        }
        PageHelper.startPage(pageNum,pageSize);
        QueryWrapper<Depart>qw=new QueryWrapper<>();
        if (depart.getDepartName()!=null){
            qw.like("depart_name",depart.getDepartName());

        }
        List<Depart> list = depService.list(qw);
        PageInfo<Depart>pageInfo=new PageInfo<>(list);
        model.addAttribute("pageInfo",pageInfo);
        return "admin-dep-list";
    }

    @RequestMapping("preSaveDep")
    //`preSaveDep()`方法用于在前端页面显示一个表单，用于添加部门信息。
    //这些功能通过使用`@RequestMapping`注解将方法与相应的URL映射起来，实现了前后端的交互。
    public String preSaveDep(){
        return "admin-dep-save";
    }

    @RequestMapping("saveDep")
    // `saveDep()`方法用于在提交表单后，将新增的部门信息保存到数据库中。
    public String saveDep(Depart depart){
        boolean save = depService.save(depart);
        return "redirect:/dep/listDep";
    }

    @RequestMapping("preUpdateDep/{id}")
    //`preUpdateDep()`方法用于在前端页面显示一个表单，用于修改已存在的部门信息。
    public String preUpdateDep(@PathVariable Integer id,Model model){

        Depart depart = depService.getById(id);
        model.addAttribute("depart",depart);
        return "admin-dep-update";
    }

    @RequestMapping("updateDep")
    //`updateDep()`方法用于在提交修改后的表单后，将修改的部门信息保存到数据库中。
    public String updateDep(Depart depart){
        boolean b = depService.updateById(depart);
        return "redirect:/dep/listDep";
    }
    //`delDep()`方法删除单个部门
    @RequestMapping("delDep/{id}")
    public String delDep(@PathVariable Integer id){
        boolean b = depService.removeById(id);
        return "redirect:/dep/listDep";
    }

    //`batchDeleteDep()`方法批量删除部门。
    @RequestMapping("batchDeleteDep")
    @ResponseBody
    public String batchDeleteDep(String idList){
        String[] split = StrUtil.split(idList, ",");
        List<Integer>list=new ArrayList<>();
        for (String s : split) {
            if (!s.isEmpty()){
                int i = Integer.parseInt(s);
                list.add(i);
            }
        }
        boolean b = depService.removeByIds(list);
        if (b){
            return "OK";

        }else {
            return "error";
        }
    }
}
