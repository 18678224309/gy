package com.jtfu.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HtmlController {

    @GetMapping("/home.html")
    public String homeHtml(){

        return  "home";
    }

    @GetMapping("/addArticle.html")
    public String homePage(){

        return  "addArticle";
    }

    @GetMapping("/center.html")
    public String centerHtml(){

        return "center";
    }

    @GetMapping("/userCenter.html")
    public String userCenterHtml(){

        return "userCenter";
    }
}
