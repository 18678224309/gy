package com.jtfu.controller;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.ICaptcha;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.core.lang.Console;
import com.jtfu.util.IdUtils;
import com.jtfu.util.R;
import com.jtfu.util.ServletUtils;
import com.jtfu.util.StringUtils;
import com.sun.org.apache.regexp.internal.RE;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@Controller
public class HtmlController {

    private static Map<String,String> verifyMap=new HashMap<>();

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

    @GetMapping("/myArticle.html")
    public String myArticleHtml(){
        return "myArticle";
    }

    @GetMapping("/audit.html")
    public String auditHtml(){
        return "audit";
    }

    @GetMapping("/register.html")
    public String registerHtml(){
        return "register";
    }

    @GetMapping("/genVerify")
    public void genVerify() throws IOException {
        Cookie[] cookies = ServletUtils.getRequest().getCookies();
        String gy_Cookie="";
        for (Cookie cookie : cookies) {
            if(verifyMap.containsKey(cookie.getValue())){
                gy_Cookie=cookie.getValue();
            }
        }
        ICaptcha captcha =CaptchaUtil.createShearCaptcha(100, 50, 4, 4);
        HttpServletResponse response = ServletUtils.getResponse();
        response.setContentType("text/plain; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        System.err.println(gy_Cookie);
        if(StringUtils.isEmpty(gy_Cookie)){
            String cookieCode=IdUtils.simpleUUID();
            Cookie cookie=new Cookie("gy_Cookie", cookieCode);
            cookie.setMaxAge(30 * 60);// 设置为30min
            cookie.setPath("/");
            verifyMap.put(cookie.getValue(),captcha.getCode());
            response.addCookie(cookie);
        }else{
            verifyMap.put(gy_Cookie,captcha.getCode());
        }
        ServletOutputStream outputStream = response.getOutputStream();
        captcha.write(outputStream);
        outputStream.close();
    }

    @PostMapping("/verifyCode")
    public R verifyCode(@CookieValue("gy_Cookie") String gy_Cookie,String Code){
        if(StringUtils.isEmpty(Code)){
            return R.error();
        }
        String s = verifyMap.get(gy_Cookie);
        if(s.equals(Code)){
            return R.success();
        }else {
            return R.error("验证码输入错误！");
        }
    }
}
