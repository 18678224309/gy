package com.jtfu.controller;


import com.jtfu.entity.Message;
import com.jtfu.entity.User;
import com.jtfu.mapper.MessageMapper;
import com.jtfu.util.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
@RequestMapping("/message")
public class MessageController {


    @Autowired
    MessageMapper messageMapper;

    @PostMapping("/addMessage")
    public R addMessage(@RequestParam int articleid, @RequestParam String message, HttpSession session){
        Message message1=new Message();
        message1.setCreatetime(new Date());
        message1.setArticleid(articleid);
        message1.setMessage(message);
        User user= (User) session.getAttribute("userInfo");
        if(user!=null){
            message1.setReplyid(user.getId());
            messageMapper.insert(message1);
            return R.success();
        }else{
            return R.error("未获取到用户登录信息！");
        }
    }

}
