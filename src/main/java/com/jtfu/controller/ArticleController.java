package com.jtfu.controller;


import com.jtfu.entity.Article;
import com.jtfu.service.IArticleService;
import com.jtfu.util.R;
import com.sun.org.apache.regexp.internal.RE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
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
        File file=new File(url+"/"+photos.getOriginalFilename());
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
