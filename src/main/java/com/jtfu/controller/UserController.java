package com.jtfu.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jtfu.entity.User;
import com.jtfu.mapper.UserMapper;
import com.jtfu.service.IUserService;
import com.jtfu.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
/*注册*/
    @RequestMapping("/register")
    public int register(@RequestParam("username")String uname,@RequestParam("password")String pwd,@RequestParam("phone")String phone){
        System.out.println(uname+"---"+pwd+"---"+phone);
        return 1;
    }
/*登录*/
    @RequestMapping("/login")
    public int login(@RequestParam("username")String uname,@RequestParam("password")String pwd){
        System.out.println(uname+"-----"+pwd);
        QueryWrapper qw = new QueryWrapper();
        qw.eq("username",uname);
        qw.eq("password",pwd);
        return 1;
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
}
