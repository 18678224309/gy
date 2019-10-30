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
import org.springframework.transaction.annotation.Transactional;
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
public class MsgboxController {

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
     * @param uid: uid //对方用户ID
     *             ,from_group: from_group //对方设定的好友分组
     *             ,group: group //我设定的好友分组
     *             ,msgboxid:msgboxid //消息盒子id
     *             ,from:userId //我的用户Id
     * @return
     */
    @PostMapping("/agreeFriend")
    @ResponseBody
    @Transactional
    public R pass(@RequestParam int uid,@RequestParam int from_group,@RequestParam int group,@RequestParam int msgboxid,@RequestParam int from) throws IOException {
       //查询我的 group分组下的好友列表，将新的好友加入。
        QueryWrapper queryWrapper=new QueryWrapper();
        queryWrapper.eq("userid",from);
        queryWrapper.eq("groupid",group);
        UserGroup userGroup=userGroupMapper.selectOne(queryWrapper);
        StringBuilder builder=new StringBuilder(userGroup.getUids());
        builder.append(",");
        builder.append(uid);
        userGroup.setUids(builder.toString());
        userGroupMapper.updateById(userGroup);
        //------------------------------------
        //查询对方的好友列表，将我加入对方的好友列表中
        queryWrapper=new QueryWrapper();
        queryWrapper.eq("userid",uid);
        queryWrapper.eq("groupid",from_group);
        userGroup=userGroupMapper.selectOne(queryWrapper);
        builder=new StringBuilder(userGroup.getUids());
        builder.append(",");
        builder.append(from);
        userGroup.setUids(builder.toString());
        userGroupMapper.updateById(userGroup);
        //---------------------------------------
        //查询出这条消息记录并删除，返回新的消息记录;
        Msgbox msgbox= msgboxMapper.selectById(msgboxid);
        if(msgbox!=null){ msgboxMapper.deleteById(msgboxid);}
        User user=userMapper.selectById(from);
        msgbox=new Msgbox();
        msgbox.setUid(uid);
        msgbox.setTime(new Date());
        msgbox.setStatus(0);
        msgbox.setContent(user.getName()+"   通过了您的请求！！");
        msgboxMapper.insert(msgbox);
        sendMsgboxNum(uid,msgboxMapper,from,from_group);
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
        sendMsgboxNum(uid,msgboxMapper,null,1);
        return r;
    }

    /**
     *
     * @param uid 要给谁发送消息
     * @param from_group 加入哪个分组
     * @param remark   备注
     * @param session
     * @return
     * @throws IOException
     */
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
        //查询当前的添加消息是否已存在；
        QueryWrapper msgboxWrapper=new QueryWrapper();
        msgboxWrapper.eq("uid",uid);
        msgboxWrapper.eq("from_group",from_group);
         if(msgboxMapper.selectOne(msgboxWrapper)!=null){
             return R.error("消息已经发送，请等待回复！");
         }
        R r=R.success();
        //接收到前端传来的添加请求后，保存至数据库未读消息
        Msgbox msgbox=new Msgbox();
        msgbox.setRemark(remark);
        msgbox.setContent("请求添加你为好友！");
        msgbox.setFrom(user.getId());
        msgbox.setFromGroup(from_group);
        msgbox.setHref("0");
        msgbox.setRead("0");
        msgbox.setStatus(0);
        msgbox.setTime(new Date());
        msgbox.setUid(uid);
        msgboxMapper.insert(msgbox);
        sendMsgboxNum(uid,msgboxMapper,null,1);
        r.set("code",0);
        return r;
    }


    public void sendMsgboxNum(int uid, MsgboxMapper msgboxMapper,Integer from,Integer from_group) throws IOException {
        Map<String, WebSocketSession> map= MyWebSocketHander.USER_ONLINE;
        //判断一下对方是否在线，不在线则不发送消息。
        if(map.containsKey(String.valueOf(uid))){
            Map msgMap=new HashMap();
            if(from!=null){
                User user=userMapper.selectById(from);
                msgMap.put("user",user);
                msgMap.put("from_group",from_group);
            }
            msgMap.put("msgboxNum",msgboxMapper.getMsgCount(uid));
            TextMessage testMsg = new TextMessage(JSON.toJSONString(msgMap));
            map.get(String.valueOf(uid)).sendMessage(testMsg);
        }
    }
}
