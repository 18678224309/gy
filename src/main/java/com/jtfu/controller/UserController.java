package com.jtfu.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jtfu.entity.User;
import com.jtfu.entity.UserGroup;
import com.jtfu.mapper.UserMapper;
import com.jtfu.service.IUserGroupService;
import com.jtfu.service.IUserService;
import com.jtfu.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author jtfu
 * @since 2019-10-22
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    IUserService userService;
    @Autowired
    IUserGroupService userGroupService;
    /*注册*/
    @RequestMapping("/register")
    public boolean register(@RequestParam("username")String username,@RequestParam("password")String pwd,@RequestParam("phone")String phone){
        System.out.println(username+"---"+pwd+"---"+phone);
        User user = new User();
        user.setUsername(username);
        user.setPassword(pwd);
        user.setPhone(phone);
        user.setStatus("online");
        user.setSign("在深邃的编码世界，做一枚轻盈的纸飞机");
        user.setAvatar("http://img1.xcarimg.com/motonews/24455/32021/32049/20180906172744162560010864435.jpg");
        user.setAge(0);
        user.setSex("1");
        boolean res = userService.save(user);
        return res;
    }
    /*登录*/
    @RequestMapping("/login")
    public int login(@RequestParam("username")String uname, @RequestParam("password")String pwd, HttpSession session){
        System.out.println(uname+"-----"+pwd);
        QueryWrapper qw = new QueryWrapper();
        qw.eq("username",uname);
        qw.eq("password",pwd);
        int res = userService.count(qw);
        if(res == 1){
            User user = userService.getOne(qw);
            session.setAttribute("userInfo",user);
        }
        System.out.println(res+"=--------------------------");
        return res;
    }

    /*判断用户名和电话是否重复*/
    @RequestMapping("/isRepeatName")
    public int isRepeatName(@RequestParam("username")String uname){
        if(uname != null && uname != ""){
            System.out.println(uname+"----");
            QueryWrapper qw = new QueryWrapper();
            qw.eq("username",uname);
            int res = userService.count(qw);
            System.out.println(res+"------------------------------");
            if(res == 0){
                return 1;
            }else{
                // 用户名存储在
                return 11;
            }
        }
        return 99;
    }
    @RequestMapping("/isRepeatPhone")
    public int isRepeatPhone(@RequestParam("phone")String phone){
        if(phone != null && phone != ""){
            System.out.println(phone+"----");
            QueryWrapper qw = new QueryWrapper();
            qw.eq("phone",phone);
            int res = userService.count(qw);
            System.out.println(res+"------------------------------");
            if(res == 0){
                return 2;
            }else{
                // 用户名存储在
                return 22;
            }
        }
        return 99;
    }

    //退出登录
    @RequestMapping("/ouLogin")
    public void outLogin(HttpSession session){
        session.removeAttribute("userInfo");
    }
}
