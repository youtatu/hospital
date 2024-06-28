package com.qf.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.qf.pojo.*;
import com.qf.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("doctor")
public class DocController {

    @Value("${location}")
    private String location;
    @Autowired
    private DocService docService;
    @Autowired
    private HosService  hosService;
    @Autowired
    private HfileService hfileService;
    @Autowired
    private DepService depService;
    @Autowired
    private VisitService visitService;

    //这段代码实现了医生列表的功能。
    @RequestMapping("listDoctor")
    //它使用了Spring MVC的`@RequestMapping`注解来映射请求路径为"/listDoctor"。
    public String listDoctor(@RequestParam(required = false,value = "pageNum",defaultValue = "1")Integer pageNum,
                             @RequestParam(required = false,value = "pageSize",defaultValue = "10")Integer pageSize, Model model, Doctor doctor){
        // 通过传入的请求参数pageNum和pageSize来指定页面的页数和每页显示的条数，默认值分别为1和10。
        if (pageNum==null || pageNum<=0 || pageNum.equals("")){
            pageNum=1;
        }
        if (pageSize==null || pageSize<=0 || pageSize.equals("")){
            pageSize=10;
        }
        PageHelper.startPage(pageNum,pageSize);// 然后通过PageHelper插件对查询进行分页处理。
        // 根据传入的doctor对象的dname属性进行模糊查询，获取匹配的医生列表。
        QueryWrapper<Doctor> qw =new QueryWrapper<>();
        if (doctor.getDname()!=null){
            qw.like("dname",doctor.getDname());
        }
        List<Doctor> list1 = docService.list(qw);
        // 接着遍历列表中的每个医生对象，并根据医生对象的hid属性在医院表中查询对应的医院，将医院名称设置到医生对象中。
        for (Doctor doctor1 : list1) {
            Integer hid = doctor1.getHid();
            Hospital byId = hosService.getById(hid);
            doctor1.setHospital(byId.getHname());
        }
        PageInfo<Doctor>pageInfo=new PageInfo<>(list1);
        List<Hospital> list = hosService.list(null);
        // 最后将医生列表和分页信息存入model对象中，返回"admin-doc-list"视图页面。
        model.addAttribute("hosList",list);
        model.addAttribute("pageInfo",pageInfo);
        return "admin-doc-list";
    }

    @RequestMapping("preSaveDoctor")//实现添加医生信息界面
    //当用户访问"/preSaveDoctor"路径时，该方法会被调用。
    // 它通过调用depService和hosService的list方法获取医院和科室列表，并将它们添加到Model对象中。
    // 然后，将Model对象返回给"admin-doc-save"视图，以便在前端进行展示和使用。
    public String preSaveDoctor(Model model){

        List<Depart> depList = depService.list(null);
        List<Hospital> hosList = hosService.list(null);
        model.addAttribute("hosList",hosList);
        model.addAttribute("depList",depList);
        return "admin-doc-save";
    }

    @RequestMapping("saveDoctor")
    //实现了保存医生信息的保存按钮
    //这段代码实现了保存医生信息的功能。具体包括：
    //1. 获取医生对象和上传的文件。
    //2. 通过调用`transfile()`方法，将上传的文件保存到指定的文件夹中，并将保存的文件名设置到医生对象中的`dimage`属性。
    //3. 调用`docService.save(doctor)`保存医生对象到数据库中。
    //4. 最后，重定向到医生列表页面。
    public String saveDoctor(Doctor doctor, MultipartFile file){
        transfile(doctor,file);
        boolean save = docService.save(doctor);
        return "redirect:/doctor/listDoctor";
    }

    private void transfile(Doctor doctor, MultipartFile file) {
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

        doctor.setDimage(path);
    }

    @RequestMapping("preUpdateDoctor/{id}")
    public String preUpdateDoctor(@PathVariable Integer id,Model model){
        Doctor byId = docService.getById(id);
        model.addAttribute("doctor",byId);

        List<Depart> depList = depService.list(null);
        List<Hospital> hosList = hosService.list(null);
        model.addAttribute("hosList",hosList);
        model.addAttribute("depList",depList);
        return "admin-doc-update";
    }


    @RequestMapping("updateDoctor")
    public String updateDoctor(Doctor doctor){
        boolean b = docService.updateById(doctor);
        return "redirect:/doctor/listDoctor";
    }

    @RequestMapping("delDoctor/{id}")
    public String delDoctor(@PathVariable Integer id){
        boolean b = docService.removeById(id);
        return "redirect:/doctor/listDoctor";
    }


    @RequestMapping("batchDeleteDoctor")
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

        boolean b = docService.removeByIds(list);
        if (b){
            return "OK";
        }else {
            return "error";
        }
    }

    @RequestMapping("listDocVisit")
    public String listDocVisit(@RequestParam(required = false,value = "pageNum",defaultValue = "1")Integer pageNum,
                               @RequestParam(required = false,value = "pageSize",defaultValue = "10")Integer pageSize, Model model, HttpSession session){
        if (pageNum==null || pageNum<=0 || pageNum.equals("")){
            pageNum=1;
        }
        if (pageSize==null || pageSize<=0 || pageSize.equals("")){
            pageSize=10;
        }
        PageHelper.startPage(pageNum,pageSize);
        Integer userId = (Integer) session.getAttribute("userId");
        List<Visit>list=visitService.listDocVisit(userId);
        for (Visit visit : list) {
            Integer did = visit.getDid();
            Doctor byId = docService.getById(did);
            Integer hid = byId.getHid();
            Hospital byId1 = hosService.getById(hid);
            Doctor doctor = visit.getDoctor();
            doctor.setHospital(byId1.getHname());
        }
        PageInfo<Visit>pageInfo=new PageInfo<>(list);
        model.addAttribute("pageInfo",pageInfo);
        return "doctor-visit-list";

    }

    @RequestMapping("confirm/{id}")
    public String confirm(@PathVariable Integer id){
        Visit byId = visitService.getById(id);
        byId.setStatus(1);
        boolean b = visitService.updateById(byId);
        return "redirect:/doctor/listDocVisit";
    }

    @RequestMapping("delMyVisit/{id}")
    public String delMyVisit(@PathVariable Integer id){
        boolean b = visitService.removeById(id);
        return "redirect:/doctor/listDocVisit";
    }


    @RequestMapping("listFile/{id}")
    public String listFile(@RequestParam(required = false,defaultValue = "1",value = "pageNum") Integer pageNum,
                           @RequestParam(required = false,defaultValue = "10",value = "pageSize") Integer pageSize, Model model, Hfile hfile, HttpSession session, @PathVariable Integer id){
        if (pageNum <= 0 || pageNum.equals("") || pageNum == null) {
            pageNum = 1;
        }
        if (pageSize <= 0 || pageSize.equals("") || pageSize == null) {
            pageSize = 10;
        }
        session.setAttribute("vid",id);
        PageHelper.startPage(pageNum, pageSize);

        Visit visit = visitService.getById(id);
        String pfile = visit.getPfile();
        String dfile = visit.getDfile();

        String[] psplit = StrUtil.split(pfile, ",");
        List<Vo>list1=new ArrayList<>();

        for (String s : psplit) {

            if (!s.isEmpty()){
                Vo vo = new Vo();
                vo.setFile(s);
                list1.add(vo);
            }
        }
        model.addAttribute("patientVo",list1);

        String[] dsplit = StrUtil.split(dfile, ",");
        List<Vo>list2=new ArrayList<>();
        for (String s : dsplit) {
            if (!s.isEmpty() ){
                Vo vo = new Vo();
                vo.setFile(s);
                list2.add(vo);
            }
        }
        model.addAttribute("doctorVo",list2);


        return "doctor-my-file-list";
    }



    @RequestMapping("preUploadFile")
    public String preUploadFile(){
        return "doctor-upload";
    }

    @RequestMapping("uploadFile")
    public String uploadFile(MultipartFile file,HttpSession session){
        Integer vid = (Integer) session.getAttribute("vid");
        Visit byId = visitService.getById(vid);

        transfile(byId,file);
        boolean b = visitService.updateById(byId);

        return "redirect:/doctor/listFile/"+vid;
    }

    private void transfile(Visit byId, MultipartFile file) {

        String originalFilename = file.getOriginalFilename();
        int index = originalFilename.lastIndexOf(".");
        String suffix = originalFilename.substring(index);
        String filename = originalFilename.substring(0, index);
        String prefix =System.nanoTime()+"";
        String path=filename+prefix+suffix;

        File file1 = new File(location);
        if (!file1.exists()){
            file1.mkdirs();
        }
        File file2 = new File(file1,path);
        try {
            file.transferTo(file2);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (byId.getDfile().isEmpty()){
            byId.setDfile(path);
        }else {
            byId.setDfile(byId.getDfile()+","+path);
        }

    }

    @RequestMapping("preDownloadFile")
    public String preDownloadFile(@RequestParam(required = false,value = "pageNum",defaultValue = "1")Integer pageNum,
                                  @RequestParam(required = false,value = "pageSize",defaultValue = "10")Integer pageSize, Model model,Hfile hfile){
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
        qw.eq("role",2);
        List<Hfile> list = hfileService.list(qw);
        PageInfo<Hfile>pageInfo=new PageInfo<>(list);
        model.addAttribute("pageInfo",pageInfo);
        return "doctor-file-list";
    }


}
