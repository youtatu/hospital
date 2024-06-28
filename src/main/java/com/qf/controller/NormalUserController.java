package com.qf.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.qf.pojo.*;
import com.qf.service.DocService;
import com.qf.service.HfileService;
import com.qf.service.HosService;
import com.qf.service.VisitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("normalUser")
public class NormalUserController {
    @Value("${location}")
    private String location;
    @Value("${filelocation}")
    private String filelocation;
    @Autowired
    private HosService hosService;
    @Autowired
    private HfileService hfileService;

    @Autowired
    private VisitService visitService;

    @Autowired
    private DocService docService;

    @RequestMapping("listDoctor")
    public String listDoctor(@RequestParam(required = false,value = "pageNum",defaultValue = "1")Integer pageNum,
                             @RequestParam(required = false,value = "pageSize",defaultValue = "10")Integer pageSize, Model model, Doctor doctor){
        //分页查询医生信息
        if (pageNum==null || pageNum<=0 || pageNum.equals("")){
            pageNum=1;
        }
        if (pageSize==null || pageSize<=0 || pageSize.equals("")){
            pageSize=10;
        }
        PageHelper.startPage(pageNum,pageSize);//用于设置分页查询的页码和每页显示的数量。
        //创建了一个查询条件的封装对象
        QueryWrapper<Doctor>qw=new QueryWrapper<>();
        //if语句根据传入的医生对象的属性值进行条件判断，如果属性值不为null，则在查询条件中加入相应的条件。
        if (doctor.getDepart()!=null){
            //表示查询条件中的depart属性值模糊匹配医生对象的depart属性值，
            qw.like("depart",doctor.getDepart());
        }

        if (doctor.getHid()!=null){
            //表示查询条件中的hid属性值等于医生对象的hid属性值。
            qw.eq("hid",doctor.getHid());
        }
        //最终根据设置的分页和查询条件进行分页查询医生信息。
        List<Doctor> list1 = docService.list(qw);
        //这段代码的功能是将医生列表中的医生关联的医院信息查询出来，
        //使用for-each循环遍历list1列表中的每个医生对象(Doctor doctor1)。
        for (Doctor doctor1 : list1) {
            //通过调用doctor1对象的getHid()方法获取医生关联的医院ID。
            Integer hid = doctor1.getHid();
           // 调用hosService的getById()方法，传入医院ID参数查询医院信息，获取医院对象byId。
            Hospital byId = hosService.getById(hid);
            doctor1.setHospital(byId.getHname());
        }
        PageInfo<Doctor>pageInfo=new PageInfo<>(list1);

        List<Hospital> list = hosService.list(null);
        //并将查询结果封装到一个PageInfo对象中。然后，获取所有医院的列表，
        //并将医院列表和封装了医生列表信息的PageInfo对象添加到模型中，
        model.addAttribute("hosList",list);
        model.addAttribute("pageInfo",pageInfo);
        //最后返回一个名为"user-doctor-list"的视图页面。
        return "user-doctor-list";
    }

    /**
     * 通过前端页面传过来的id，查出来对应医生的信息
     * @param did
     * @param session
     * @return
     */
    @RequestMapping("guahao/{did}")
    public String guahao(@PathVariable Integer did, HttpSession session){
        Integer userId = (Integer) session.getAttribute("userId");
        Visit visit = new Visit();
        visit.setPid(userId);
        visit.setDid(did);
        visitService.save(visit);
        return "redirect:/normalUser/listMyVisit";
    }


    /**
     * 后台数据库搜索该用户挂号的所有医生的信息并展示在前台
     * @param session
     * @param model
     * @return
     */
    @RequestMapping("listMyVisit")
    public String listMyVisit(HttpSession session,Model model){
        //首先从session中获取当前用户的userId。
        Integer userId = (Integer) session.getAttribute("userId");
        ////然后调用visitService的listMyVisit方法，传入userId参数，获取当前用户的就诊记录列表。
        List<Visit>list=visitService.listMyVisit(userId);
        //接下来，使用一个foreach循环遍历就诊记录列表。
        // 对于每一个就诊记录，获取其对应的医生的did，再通过did获取医生的详细信息，
        //并获取医生所属的医院hid，再通过hid获取医院的详细信息。然后将医院名字设置到该就诊记录对应的医生对象中。
        for (Visit visit : list) {
            Integer did = visit.getDid();
            Doctor byId = docService.getById(did);
            Integer hid = byId.getHid();
            Hospital byId1 = hosService.getById(hid);
            Doctor doctor = visit.getDoctor();
            doctor.setHospital(byId1.getHname());
        }
        //最后，创建一个PageInfo对象，将就诊记录列表封装到PageInfo对象中，
        PageInfo<Visit>pageInfo=new PageInfo<>(list);
        //并将该对象添加到model中传递给前端页面进行展示。
        model.addAttribute("pageInfo",pageInfo);
        return "user-visit-list";
    }

    @RequestMapping("delMyVisit/{id}")
    public String delMyVisit(@PathVariable Integer id){
        boolean b = visitService.removeById(id);
        return "redirect:/normalUser/listMyVisit";
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

        return "user-my-file-list";
    }


    @RequestMapping("preUploadFile")
    public String preUploadFile(){
        return "user-upload";
    }

    @RequestMapping("uploadFile")
    public String uploadFile(MultipartFile file, HttpSession session){
        Integer vid = (Integer) session.getAttribute("vid");
        Visit byId = visitService.getById(vid);

        transfile(byId,file);
        boolean b = visitService.updateById(byId);

        return "redirect:/normalUser/listFile/"+vid;
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
        if (byId.getPfile().isEmpty()){
            byId.setPfile(path);
        }else {
            byId.setPfile(byId.getPfile()+","+path);
        }

    }

    @GetMapping("downloadPatientFile")
    public void downloadPatientFile(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        //1.获取请求参数
        String filename = request.getParameter("filename");
        String bid = request.getParameter("bid");

        File uploadFile=new File(location+"/"+filename);
        ServletOutputStream os=response.getOutputStream();
        response.addHeader("Content-Disposition","attachment;filename="+ URLEncoder.encode(filename,"UTF-8"));
        response.setContentType("application/octet-stream");
        os.write(FileUtil.readBytes(uploadFile));
        os.flush();
        os.close();


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
        qw.eq("role",3);
        List<Hfile> list = hfileService.list(qw);
        PageInfo<Hfile>pageInfo=new PageInfo<>(list);
        model.addAttribute("pageInfo",pageInfo);
        return "user-file-list";

    }

    @GetMapping("downloadFile")
    public void downloadFile(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        //1.获取请求参数
        String filename = request.getParameter("filename");
        String bid = request.getParameter("bid");

        File uploadFile=new File(filelocation+"/"+filename);
        ServletOutputStream os=response.getOutputStream();
        response.addHeader("Content-Disposition","attachment;filename="+ URLEncoder.encode(filename,"UTF-8"));
        response.setContentType("application/octet-stream");
        os.write(FileUtil.readBytes(uploadFile));
        os.flush();
        os.close();


    }

}
