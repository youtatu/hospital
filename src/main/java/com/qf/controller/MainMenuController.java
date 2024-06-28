package com.qf.controller;

import com.qf.pojo.CountNumber;
import com.qf.service.VisitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("main")
public class MainMenuController {


    @Autowired
    private VisitService visitService;

    @RequestMapping("mainMenu")
    public List<com.qf.pojo.MainMenu> mainMenu(){

        List<CountNumber> list=visitService.queryNum();

        List<com.qf.pojo.MainMenu> list1 = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            com.qf.pojo.MainMenu mainMenu = new com.qf.pojo.MainMenu();
            mainMenu.setType(list.get(i).getName());
            mainMenu.setMount(Integer.valueOf(list.get(i).getCount()));
            list1.add(mainMenu);
        }

        return list1;

    }
}
