package com.jtfu.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.jtfu.config.MyWebSocketHander;
import com.jtfu.entity.Group1;
import com.jtfu.entity.Msgbox;
import com.jtfu.entity.User;
import com.jtfu.entity.UserGroup;
import com.jtfu.mapper.MsgboxMapper;
import com.jtfu.mapper.UserGroupMapper;
import com.jtfu.service.IGroupService;
import com.jtfu.service.IUserGroupService;
import com.jtfu.service.IUserService;
import com.jtfu.util.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.socket.WebSocketSession;

import javax.servlet.http.HttpSession;
import java.util.*;

@Controller
@RequestMapping("/webSocket")
public class WebSocketController {


    @Autowired
    IUserService userService;

    @Autowired
    IUserGroupService userGroupService;

    @Autowired
    IGroupService groupService;

    @Autowired
    MsgboxMapper msgboxMapper;

    @Autowired
    UserGroupMapper userGroupMapper;

    @GetMapping("/layIm.html")
    public String layIm(Model model,HttpSession session){
        User user= (User) session.getAttribute("userInfo");
        model.addAttribute("userId",user.getId());
        return "layIm";
    }


    @GetMapping("/getList")
    @ResponseBody
    public R getList(HttpSession session){
        R r=R.success();
        //1,将登陆用户查询出来  2,得到登陆用户的所有列表，  3，将列表下的所有人员列出
        User user= (User) session.getAttribute("userInfo");

        //查询用户所拥有的分组id
        QueryWrapper userGroupW=new QueryWrapper();
        userGroupW.eq("userid",user.getId());
        List<UserGroup> userGroups=userGroupService.list(userGroupW);
        Set groupIds=new HashSet();
        Map<Integer,String> group_users=new HashMap();
        userGroups.stream().forEach(s->{
            groupIds.add(s.getGroupid());
            group_users.put(s.getGroupid(),s.getUids());
        });

        //根据分组id，查询好友分组信息
        QueryWrapper groupW=new QueryWrapper();
        if(groupIds.size()>0){groupW.in("id",groupIds);}
        groupW.eq("grouptype",1);
        List<Group1> friendList=groupService.list(groupW);
        for(int i=0;i<friendList.size();i++){
            Group1 itemGroup=friendList.get(i);
            if(group_users.containsKey(itemGroup.getId())){
                String[] ids=group_users.get(itemGroup.getId()).split(",");
                QueryWrapper userW=new QueryWrapper();
                userW.in("id",ids);
                userW.isNotNull("status");
                userW.orderByDesc("status");
               itemGroup.setList(userService.list(userW));
               friendList.set(i,itemGroup);
            }
        }


        //根据分组id，查询群组信息
        QueryWrapper groupQ=new QueryWrapper();
        if(groupIds.size()>0){groupQ.in("id",groupIds);}
        groupQ.eq("grouptype",2);
        List<Group1> groupList=groupService.list(groupQ);


        Map map=new HashMap();
        map.put("mine",user);//用户信息
        map.put("friend",friendList);//好友列表
        map.put("group",groupList);//群组列表
        r.set("data",map);
        r.set("code","0");
        return r;
    }


    @GetMapping("/getMembers")
    @ResponseBody
    public R getMembers(){

        return R.success();
    }


    @PostMapping("/add")
    @ResponseBody
    public R add(@RequestParam int uid,@RequestParam String from_group,@RequestParam String remark,HttpSession session){
        User user= (User) session.getAttribute("userInfo");
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
        r.set("code",0);
        return r;
    }
}
