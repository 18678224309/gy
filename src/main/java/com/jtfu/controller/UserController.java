package com.jtfu.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jtfu.entity.User;
import com.jtfu.mapper.UserMapper;
import com.jtfu.service.IUserGroupService;
import com.jtfu.service.IUserService;
import com.jtfu.util.R;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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

    private static List<HttpSession> list=new ArrayList<>();
    @Autowired
    IUserService userService;
    @Autowired
    UserMapper userMapper;
    @Autowired
    IUserGroupService userGroupService;
    @Value("${imageServer}")
    String imageServer;
    /*注册*/
    @RequestMapping("/register")
    public R register(User user){
        QueryWrapper wrapper=new QueryWrapper();
        wrapper.eq("username",user.getUsername());
        User one = userService.getOne(wrapper);
        if(one!=null){
            return R.error("用户名已存在！");
        }
        user.setAge(10);
        user.setRoleid(0);
        user.setStatus("online");
        user.setDelFlag(0);
        user.setCreatetime(new Date());
        user.setSign("在深邃的编码世界，做一枚轻盈的纸飞机");
        user.setAvatar("/ssm_Demo/static/image/default.jpg");
        userService.save(user);
        return R.success();
    }
    /*登录*/
    @RequestMapping("/login")
    public R login(@RequestParam("username")String uname, @RequestParam("password")String pwd, HttpSession session){
        QueryWrapper qw = new QueryWrapper();
        qw.eq("username",uname);
        qw.eq("password",pwd);
        User user = userService.getOne(qw);
        if(user!=null){
            user.setStatus("online");
            userService.updateById(user);
            session.setAttribute("userInfo",user);
            list.add(session);
            System.out.println(session.getAttribute("userInfo").toString());
            return R.success();
        }else{
            return R.error("用户名或密码不正确！");
        }
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
    @GetMapping("/outLogin")
    public void outLogin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session=request.getSession();
        User user = (User)session.getAttribute("userInfo");
        if(user!=null){
            //修改登陆状态
            user.setStatus("offline");
            session.removeAttribute("userInfo");
        }
        request.getRequestDispatcher("/index.html").forward(request, response);
        //response.sendRedirect("/index.html");
    }

    //完善个人信息
    @RequestMapping("/updateUser")
    public R perfectInfo(@RequestParam Map map,HttpSession session){
        //获得所有的属性
        Method[] methods=User.class.getDeclaredMethods();
        //对属性进行循环，与map中进行比较，如果有的话进行更新操作;
        User user= (User) session.getAttribute("userInfo");
        String name= (String) map.get("name");
        String sign= (String) map.get("sign");
        String password= (String) map.get("password");
        if(name!=null){
            user.setName(name);
        }
        if(sign!=null){
            user.setSign(sign);
        }
        if(password!=null&&!password.equals("")){
            user.setPassword(password);
        }
        userMapper.updateById(user);
        return R.success();
    }


    @PostMapping("/getHotUser")
    public R getHotUser(){
        return R.success().set("userHot",userMapper.getHotUser());
    }

    @PostMapping("/uploadImg")
    public R uploadImg(@RequestParam("uid")int uid,@RequestParam("photo")MultipartFile photo, HttpServletRequest request) throws IOException {
        String staticPath=request.getRealPath("static");
        String path=staticPath+"/image/";
        User user=userMapper.selectById(uid);
        if(user!=null){
            File file=new File(path);
            if(!file.exists()){
                file.mkdir();
            }
            String imgPath=path+"/"+photo.getOriginalFilename();
            OutputStream outputStream=new FileOutputStream(new File(imgPath));
            outputStream.write(photo.getBytes());
            outputStream.flush();
            outputStream.close();
            //String subPath=imgPath.substring(imgPath.indexOf("/static"),imgPath.length());
            //fsSource.copyFromLocalFile(new Path(imgPath),new Path("/gyImg"));
            String hadoopImgPath="/static/image/"+photo.getOriginalFilename()+"";
            user.setAvatar(hadoopImgPath);
            userMapper.updateById(user);
            request.getSession().setAttribute("userInfo",user);
            return R.success().set("imgPath",hadoopImgPath);
        }
        return R.error();
    }

}
