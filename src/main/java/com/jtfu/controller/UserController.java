package com.jtfu.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jtfu.entity.User;
import com.jtfu.entity.UserGroup;
import com.jtfu.mapper.UserMapper;
import com.jtfu.service.IUserGroupService;
import com.jtfu.service.IUserService;
import com.jtfu.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;

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
        user.setName(username);
        user.setAge(10);
        user.setSex("1");
        user.setRoleid(0);
        user.setDelFlag(0);
        user.setCreatetime(new Date());
        user.setSign("在深邃的编码世界，做一枚轻盈的纸飞机");
        user.setAvatar("/ssm_Demo/static/image/80421574-D205-41EA-9631-C72CFDA90063.jpg");
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
            user.setStatus("online");
            userService.updateById(user);
            session.setAttribute("userInfo",user);
        }
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
    @RequestMapping("/outLogin")
    public int outLogin(HttpSession session,User user){
        user = (User)session.getAttribute("userInfo");
        //修改登陆状态
        user.setStatus("offline");
        QueryWrapper qw = new QueryWrapper();
        boolean isOffline = userService.updateById(user);
        session.removeAttribute("userInfo");
        if(session.getAttribute("userInfo")==null && isOffline == true){
            return 1;
        }
        return 0;
    }

    //完善个人信息
    @RequestMapping("/perfectInfo")
    public int perfectInfo(){

        return 0;
    }

    //添加头像
    @RequestMapping("/upT")
    @ResponseBody
    public void upLodeTou(@RequestPart("photo") MultipartFile photo, HttpServletRequest request){
        System.out.println(photo.isEmpty());

    }
}
