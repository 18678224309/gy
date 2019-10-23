package com.jtfu.controller;


import com.jtfu.mapper.UserMapper;
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
    UserServiceImpl userService;

    @RequestMapping("/login")
    public int login(@RequestParam("title")String uname,@RequestParam("password")String pwd){
        System.out.println(uname+"-----"+pwd);

        return 1;
    }

//    @RequestMapping("/re")

}
