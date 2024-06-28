package com.qf.controller;

import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageInfo;
import com.qf.pojo.*;
import com.qf.service.DocService;
import com.qf.service.HosService;
import com.qf.service.PatService;
import com.qf.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.List;

@Controller
public class UserController {
    @Autowired
    private UserService userService;
    @Value("${location}")
    private String location;
    @Autowired
    private HosService hosService;
    @Autowired
    private DocService docService;
    @Autowired
    private PatService patService;
    @RequestMapping("login")
    public String login(String username, String password, Integer role, Model model, HttpSession session) {
        if (role == null) {
            model.addAttribute("msg", "请选择身份");
            return "index";
        }
        if (role == 1) {
            boolean i = userService.login(username, password);
            if (i) {
                QueryWrapper<User> qw = new QueryWrapper<>();
                qw.eq("username", username);
                User user = userService.getOne(qw);
                //存储数据
                session.setAttribute("currentUser", username);
                session.setAttribute("password", password);
                session.setAttribute("userId", user.getId());
                session.setAttribute("email", user.getEmail());
                session.setAttribute("phone", user.getPhone());
                session.setAttribute("image", user.getImage());
                session.setAttribute("role", 1);
                List<Hospital> list = hosService.list(null);
                //做分页
                PageInfo hosPageInfo = new PageInfo(list);
                model.addAttribute("hosPageInfo", hosPageInfo);

                List<Doctor> list1 = docService.list(null);
                PageInfo hosPageInfo1 = new PageInfo(list1);
                //将数据返回给前端
                model.addAttribute("docPageInfo", hosPageInfo1);

                return "gymMainMenu";//管理员首页
            }else {
                model.addAttribute("msg","用户名密码错误");
                return "index";
            }
        } else if (role == 2) {
            boolean i = docService.login(username, password);
            if (i){
                QueryWrapper<Doctor> qw = new QueryWrapper<>();
                qw.eq("dname", username);
                Doctor user = docService.getOne(qw);
                session.setAttribute("currentUser", username);
                session.setAttribute("password", password);
                session.setAttribute("userId", user.getId());
                session.setAttribute("depart", user.getDepart());
                session.setAttribute("phone", user.getPhone());
                session.setAttribute("image", user.getDimage());
                session.setAttribute("role", 2);
                List<Hospital> list = hosService.list(null);
                PageInfo hosPageInfo = new PageInfo(list);
                model.addAttribute("hosPageInfo", hosPageInfo);

                List<Doctor> list1 = docService.list(null);
                PageInfo hosPageInfo1 = new PageInfo(list1);
                model.addAttribute("docPageInfo", hosPageInfo1);
            }else {
                model.addAttribute("msg","用户名密码错误");
                return "index";
            }


            return "gymMainMenu1";
        } else {

            boolean i = patService.login(username, password);
            if (i){
                QueryWrapper<Patient> qw = new QueryWrapper<>();
                qw.eq("pname", username);
                Patient user = patService.getOne(qw);
                session.setAttribute("currentUser", username);
                session.setAttribute("password", password);
                session.setAttribute("userId", user.getId());
                session.setAttribute("address", user.getAddress());
                session.setAttribute("image", user.getPimage());
                session.setAttribute("phone", user.getPhone());
                session.setAttribute("role", 3);

                List<Hospital> list = hosService.list(null);
                PageInfo hosPageInfo = new PageInfo(list);
                model.addAttribute("hosPageInfo", hosPageInfo);

                List<Doctor> list1 = docService.list(null);
                PageInfo hosPageInfo1 = new PageInfo(list1);
                model.addAttribute("docPageInfo", hosPageInfo1);
            }else {
                model.addAttribute("msg","用户名密码错误");
                return "index";
            }
            return "gymMainMenu2";
        }

    }

    @RequestMapping("count")
    public String count(){
        return "chart_count";
    }

    @RequestMapping("pwd")
    public String pwd(){
        return "modify";
    }


    @RequestMapping("pwdUser")
    public String pwdUser(String userPwd,String newPwd,HttpSession session,Model model){
        String currentUser = (String) session.getAttribute("currentUser");
        boolean login = userService.login(currentUser, userPwd);
        if (login){
            User user = new User();
            user.setUsername(currentUser);
            String s = DigestUtil.md5Hex(newPwd);
            user.setPassword(s);
            QueryWrapper<User>qw=new QueryWrapper<>();
            qw.eq("username",currentUser);
            boolean update = userService.update(user, qw);
            return "index";

        }else {
            model.addAttribute("loginFail","用户验证失败");
            return "modify";
        }
    }

    @RequestMapping("logout")
    public String logout(){
        return "index";
    }

    @RequestMapping("toRegister")
    public String toRegister(){
        return "register";
    }

    @RequestMapping("toLogin")
    public String toLogin(){
        return "index";
    }

    @RequestMapping("register")
    public String register(String userName,String userPwd,String confirmPwd,Model model){

        QueryWrapper<Patient>qw=new QueryWrapper<>();
        qw.eq("pname",userName);
        Patient one = patService.getOne(qw);
        if (one!=null){
            model.addAttribute("msg","该用户已存在");
            return "register";
        }
        if (!userPwd.equals(confirmPwd)){
            model.addAttribute("msg","两次输入的密码不一致");
            return "register";
        }

        Patient user = new Patient();
        user.setPname(userName);
        user.setPassword(DigestUtil.md5Hex(userPwd));
        boolean save = patService.save(user);
        return "index";


    }

    @RequestMapping("adminHome")
    public String adminHome(Model model){
        List<Hospital> list = hosService.list(null);
        PageInfo hosPageInfo = new PageInfo(list);
        model.addAttribute("hosPageInfo", hosPageInfo);

        List<Doctor> list1 = docService.list(null);
        PageInfo hosPageInfo1 = new PageInfo(list1);
        model.addAttribute("docPageInfo", hosPageInfo1);
        return "gymMainMenu";
    }

    @RequestMapping("doctorHome")
    public String doctorHome(Model model){
        List<Hospital> list = hosService.list(null);
        PageInfo hosPageInfo = new PageInfo(list);
        model.addAttribute("hosPageInfo", hosPageInfo);

        List<Doctor> list1 = docService.list(null);
        PageInfo hosPageInfo1 = new PageInfo(list1);
        model.addAttribute("docPageInfo", hosPageInfo1);
        return "gymMainMenu1";
    }

    @RequestMapping("patientHome")
    public String patientHome(Model model){
        List<Hospital> list = hosService.list(null);
        PageInfo hosPageInfo = new PageInfo(list);
        model.addAttribute("hosPageInfo", hosPageInfo);

        List<Doctor> list1 = docService.list(null);
        PageInfo hosPageInfo1 = new PageInfo(list1);
        model.addAttribute("docPageInfo", hosPageInfo1);
        return "gymMainMenu2";
    }

    @PostMapping("login1")
    @ResponseBody
    public Result login(@RequestBody User user, HttpSession session){
        Result result = new Result();
        boolean login = docService.login(user.getUsername(), user.getPassword());
        if (login){
            result.setFlag(true);
            session.setAttribute("user",user.getUsername());
            QueryWrapper<Doctor>qw=new QueryWrapper<>();
            qw.eq("dname",user.getUsername());
            qw.eq("password",user.getPassword());
            Doctor one = docService.getOne(qw);
            session.setAttribute("image",one.getDimage());
        }else {
            result.setFlag(false);
            result.setMessage("登录失败");
        }
        return result;
    }

    @GetMapping("/getUsername")
    @ResponseBody
    public User getDocname(HttpSession session) {

        String username = (String) session.getAttribute("user");
        String image = (String) session.getAttribute("image");
        User user = new User();
        user.setUsername(username);
        user.setImage(image);
        return user;
    }

    @GetMapping("/getPatname")
    @ResponseBody
    public Patient getPatname(HttpSession session) {

        String username = (String) session.getAttribute("currentUser");
        String image = (String) session.getAttribute("image");
        Patient patient = new Patient();
        patient.setPname(username);
        patient.setPimage(image);
        return patient;
    }

    @RequestMapping("chat")
    public String DocChat(){
        return "chat";
    }

    @RequestMapping("user_chat")
    public String PatChat(){
        return "user_chat";
    }

    @RequestMapping("profile")
    public String profile(HttpSession session,Model model){
        Integer role = (Integer) session.getAttribute("role");
        String currentUser = (String) session.getAttribute("currentUser");
        String password = (String) session.getAttribute("password");
        Integer userId = (Integer) session.getAttribute("userId");
        if (role==1){
            User byId = userService.getById(userId);
            byId.setPassword(password);
            model.addAttribute("user",byId);
            return "profile-admin";
        }else if (role==2){
            Doctor byId = docService.getById(userId);
            byId.setPassword(password);
            model.addAttribute("user",byId);
            return "profile-doctor";
        }else {
            Patient byId = patService.getById(userId);
            byId.setPassword(password);
            model.addAttribute("user",byId);
            return "profile-patient";
        }
    }

    @RequestMapping("updateAdminProfile")
    public String updateAdminProfile(User user, MultipartFile file){
        if (!file.isEmpty()){
            transfileAdmin(user,file);
        }
        user.setPassword(DigestUtil.md5Hex(user.getPassword()));
        boolean b = userService.updateById(user);
        return "redirect:/profile";
    }

    private void transfileAdmin(User user, MultipartFile file) {
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

        user.setImage(path);
    }

    @RequestMapping("updateDoctorProfile")
    public String updateDoctorProfile(Doctor doctor, MultipartFile file){
        if (!file.isEmpty()){
            transfileDoctor(doctor,file);
        }
        doctor.setPassword(DigestUtil.md5Hex(doctor.getPassword()));
        boolean b = docService.updateById(doctor);
        return "redirect:/profile";
    }

    private void transfileDoctor(Doctor doctor, MultipartFile file) {
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

    @RequestMapping("updatePatientProfile")
    public String updatePatientProfile(Patient patient, MultipartFile file){
        if (!file.isEmpty()){
            transfilePatient(patient,file);
        }
        patient.setPassword(DigestUtil.md5Hex(patient.getPassword()));
        boolean b = patService.updateById(patient);
        return "redirect:/profile";
    }

    private void transfilePatient(Patient patient, MultipartFile file) {
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

        patient.setPimage(path);
    }



}
