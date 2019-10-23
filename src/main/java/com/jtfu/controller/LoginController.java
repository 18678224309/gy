package com.jtfu.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/home.html")
    public String homeHtml(Model model){

        return  "home";
    }
}
