package com.jtfu.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.jtfu.config.MyWebSocketHander;
import com.jtfu.entity.Msgbox;
import com.jtfu.entity.User;
import com.jtfu.entity.UserGroup;
import com.jtfu.mapper.MsgboxMapper;
import com.jtfu.mapper.UserGroupMapper;
import com.jtfu.mapper.UserMapper;
import com.jtfu.util.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/msgbox")
public class MsgboxController extends MyWebSocketHander{

    @Autowired
    MsgboxMapper msgboxMapper;
    @Autowired
    UserMapper userMapper;

    @Autowired
    UserGroupMapper userGroupMapper;


    @GetMapping("/getMsgboxByUid")
    @ResponseBody
    public R getMsgboxByUid(@RequestParam int uid){
        R r=R.success();
        QueryWrapper queryWrapper=new QueryWrapper();
        queryWrapper.eq("uid",uid);
        queryWrapper.orderByDesc("time");
        List<Msgbox>  msgboxList=msgboxMapper.selectList(queryWrapper);
        //为了方便给前端传值，把消息的发起用户填充至模型中
        for(int i=0;i<msgboxList.size();i++){
            Msgbox msgbox=msgboxList.get(i);
            msgbox.setUser(userMapper.selectById(msgbox.getFrom()));
            msgboxList.set(i,msgbox);
        }
        r.set("pages",1);
        r.set("code",0);
        r.set("data",msgboxList);
        return r;
    }

    @GetMapping("/getMsgCount")
    @ResponseBody
    public int getMsgCount(@RequestParam int uid){
         return msgboxMapper.getMsgCount(uid);
    }

    /**
     * 只要打开消息盒子，所有给自己的消息置为已读：
     * @param uid 用户id
     * @return
     */
    @GetMapping("/yd")
    @ResponseBody
    public void yd(@RequestParam int uid){
        QueryWrapper queryWrapper=new QueryWrapper();
        queryWrapper.eq("uid",uid);
        queryWrapper.eq("status1",0);
        List<Msgbox> msgboxes= msgboxMapper.selectList(queryWrapper);
        msgboxes.stream().forEach(s->{
            s.setStatus(1);
            msgboxMapper.updateById(s);
        });
    }

    /**
     * 通过请求
     * @param map
     * @return
     */
    @PostMapping("/pass")
    @ResponseBody
    public R pass(@RequestParam Map map){
       R r=R.success();
       return r;
    }


    /**
     * 拒绝
     * @param uid 对方的用户id，from自己的用户id  msgboxid 消息盒子的id
     * @return
     */
    @PostMapping("/refuse")
    @ResponseBody
    public R refuse(@RequestParam int uid,@RequestParam int from,@RequestParam int msgboxid) throws IOException {
        User user=userMapper.selectById(from);
        R r=R.success();
        Msgbox msgbox= msgboxMapper.selectById(msgboxid);
        if (msgbox==null||user==null){return R.error();}
        msgboxMapper.deleteById(msgboxid);
        msgbox=new Msgbox();
        msgbox.setUid(uid);
        msgbox.setTime(new Date());
        msgbox.setStatus(0);
        msgbox.setContent(user.getName()+"   拒绝了您的请求！！");
        msgboxMapper.insert(msgbox);
        super.sendMsgboxNum(uid,msgboxMapper);
        return r;
    }

    @PostMapping("/add")
    @ResponseBody
    public R add(@RequestParam int uid, @RequestParam String from_group, @RequestParam String remark, HttpSession session) throws IOException {
        User user= (User) session.getAttribute("userInfo");
        //查询用户不存在，或者添加自己时返回错误信息;
        if(user==null||user.getId()==uid){
            return R.error("未获取到用户信息！");
        }
        //查询当前用户的好友信息，避免重复添加
        QueryWrapper userGroupWrapper=new QueryWrapper();
        userGroupWrapper.eq("userid",uid);
        List<UserGroup> list=userGroupMapper.selectList(userGroupWrapper);
        for(int i=0;i<list.size();i++){
            UserGroup userGroup=list.get(i);
            String[] uids=userGroup.getUids().split(",");
            for(int j=0;j<uids.length;j++){
                if(uids[i].equals(user.getId().toString())){
                    return R.error("已经添加好友，请勿重复添加！");
                }
            }
        }
        R r=R.success();
        //接收到前端传来的添加请求后，保存至数据库未读消息
        Msgbox msgbox=new Msgbox();
        msgbox.setRemark(remark);
        msgbox.setContent("请求添加你为好友！");
        msgbox.setFrom(user.getId());
        msgbox.setFrom_group(from_group);
        msgbox.setHref("0");
        msgbox.setRead("0");
        msgbox.setStatus(0);
        msgbox.setTime(new Date());
        msgbox.setUid(uid);
        msgboxMapper.insert(msgbox);
        super.sendMsgboxNum(uid,msgboxMapper);
        r.set("code",0);
        return r;
    }
}
