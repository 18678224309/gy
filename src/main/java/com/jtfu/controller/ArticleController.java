package com.jtfu.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jtfu.entity.Article;
import com.jtfu.service.IArticleService;
import com.jtfu.util.R;
import com.sun.corba.se.spi.orbutil.threadpool.WorkQueue;
import com.sun.org.apache.regexp.internal.RE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;


@Controller
@RequestMapping("/article")
public class ArticleController {

    @Autowired
    IArticleService articleService;

    @PostMapping("/uoloadImg")
    @ResponseBody
    public R uoloadImg(@RequestPart("photos") MultipartFile photos,@RequestParam("url") String url, HttpServletRequest request) throws Exception {
        String pathName=url+"/"+photos.getOriginalFilename();
        System.err.println(pathName);
        File file=new File(pathName);
        OutputStream outputStream=new FileOutputStream(file);
        outputStream.write(photos.getBytes());
        outputStream.flush();
        outputStream.close();
        return R.success();
    }

    @PostMapping("/genKey")
    @ResponseBody
    public R genKey(HttpServletRequest request){
        String uuid=UUID.randomUUID().toString();
        String staticPath=request.getRealPath("static");
        String path=staticPath+"\\image\\"+uuid;
        File file=new File(path);
        if(!file.exists()){
            file.mkdir();
        }
        return R.success().set("key",path);
    }

    @PostMapping("/saveArticle")
    @ResponseBody
    public R saveArticle(String title,String describe,int money,String urlPath){
        Article article=new Article();
        article.setTitle(title);
        article.setAuth("取Session用户名");
        article.setAuthId(0);
        article.setMoney(money);
        article.setDescribe(describe);
        article.setPhotos(urlPath);
        article.setCreatetime(new Date());
        article.setStatus(1);
        articleService.save(article);
        return R.success();
    }

    @PostMapping("/removeImg")
    @ResponseBody
    public R removeImg(@RequestParam("url")String url){
        File file=new File(url);
        if(file.exists()){
            delAllFile(file);
        }
        return R.success();
    }



    @PostMapping("/getList")
    @ResponseBody
    public R getList(@RequestParam Map map){
        Field[] fields= Article.class.getDeclaredFields();
        QueryWrapper queryWrapper=new QueryWrapper();
        Page page=new Page();
        page.setCurrent(Long.valueOf(map.get("curr").toString()));
        page.setSize(Long.valueOf( map.get("limit").toString()));

        for (int i = 0; i < fields.length; i++) {
            Field field= fields[i];
            String fieldName=field.getName();
            if(map.containsKey(fieldName)){
                queryWrapper.eq(fieldName,map.get(fieldName));
            }
        }
        articleService.page(page,queryWrapper);
        return R.success().set("page",page);
    }


    @PostMapping("/getSlideShow")
    @ResponseBody
    public R getSlideShow(){
        QueryWrapper wrapper=new QueryWrapper();
        wrapper.eq("status",1);
        wrapper.isNotNull("photos");
        wrapper.ne("photos","");
        wrapper.orderByAsc("createtime");
        List<Article> list= articleService.list(wrapper);
        list.subList(0,5);
        for (int i=0;i<list.size();i++){
            Article article=list.get(i);
            String photoDir= article.getPhotos();
            File file=new File(photoDir);
            File[] files= file.listFiles();
            article.setPhotos(photoDir.substring(photoDir.indexOf("static"),photoDir.length())+"\\"+files[0].getName());
            list.set(i,article);
        }

        return R.success().set("SlideShow",list);
    }


    /**
     * 删除文件或文件夹
     * @param directory
     */
    public static void delAllFile(File directory){
        if (!directory.isDirectory()){
            directory.delete();
        } else{
            File [] files = directory.listFiles();

            // 空文件夹
            if (files.length == 0){
                directory.delete();
                System.out.println("删除" + directory.getAbsolutePath());
                return;
            }

            // 删除子文件夹和子文件
            for (File file : files){
                if (file.isDirectory()){
                    delAllFile(file);
                } else {
                    file.delete();
                    System.out.println("删除" + file.getAbsolutePath());
                }
            }

            // 删除文件夹本身
            directory.delete();
            System.out.println("删除" + directory.getAbsolutePath());
        }
    }
}
