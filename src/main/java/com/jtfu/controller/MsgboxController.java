package com.jtfu.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.jtfu.entity.Msgbox;
import com.jtfu.entity.User;
import com.jtfu.mapper.MsgboxMapper;
import com.jtfu.mapper.UserMapper;
import com.jtfu.util.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/msgbox")
public class MsgboxController {

    @Autowired
    MsgboxMapper msgboxMapper;
    @Autowired
    UserMapper userMapper;

    @GetMapping("/getMsgboxByUid")
    @ResponseBody
    public R getMsgboxByUid(@RequestParam int uid){
        R r=R.success();
        QueryWrapper queryWrapper=new QueryWrapper();
        queryWrapper.eq("uid",uid);
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
        QueryWrapper queryWrapper=new QueryWrapper();
        queryWrapper.eq("uid",uid);
        queryWrapper.eq("status1",0);
         return msgboxMapper.selectCount(queryWrapper);
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
     * @param uid 对方的用户id，from自己的用户id
     * @return
     */
    @PostMapping("/refuse")
    @ResponseBody
    public R refuse(@RequestParam int uid,@RequestParam int from){
        User user=userMapper.selectById(from);
        R r=R.success();
        QueryWrapper queryWrapper=new QueryWrapper();
        queryWrapper.eq("from1",uid);
        queryWrapper.eq("uid",from);
        Msgbox msgbox= msgboxMapper.selectOne(queryWrapper);
        if (msgbox==null||user==null){return R.error();}
        msgboxMapper.delete(queryWrapper);
        msgbox=new Msgbox();
        msgbox.setUid(uid);
        msgbox.setTime(new Date());
        msgbox.setStatus(0);
        msgbox.setContent(user.getName()+"   拒绝了您的请求！！");
        msgboxMapper.insert(msgbox);
        return r;
    }
}
