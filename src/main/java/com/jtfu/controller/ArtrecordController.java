package com.jtfu.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jtfu.service.IArtrecordService;
import com.jtfu.util.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/artrecord")
public class ArtrecordController {

    @Autowired
    IArtrecordService artrecordService;


    @GetMapping("/list")
    public R list(Integer articleId){
        QueryWrapper wrapper=new QueryWrapper();
        wrapper.eq("articleid",articleId);
        return R.success().set("list",artrecordService.list());
    }
}
