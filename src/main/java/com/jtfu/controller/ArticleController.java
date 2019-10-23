package com.jtfu.controller;


import com.jtfu.util.R;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;


@Controller
@RequestMapping("/article")
public class ArticleController {

    @PostMapping("/uoloadImg")
    @ResponseBody
    public R uoloadImg(@RequestPart("photos") MultipartFile photos,@RequestParam("url") String url, HttpServletRequest request) throws Exception {
        File file;
        String path="";
        if(url.equals("first")){
            String staticPath=request.getRealPath("static");
            SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
            path=staticPath+"\\image\\"+UUID.randomUUID().toString();
            file=new File(path);
            if(!file.exists()){
                file.mkdir();
            }
            file=new File(path+"/"+ photos.getOriginalFilename());
        }else{
            file=new File(url+"/"+ photos.getOriginalFilename());
            path=url;
        }

        OutputStream outputStream=new FileOutputStream(file);
        outputStream.write(photos.getBytes());
        outputStream.flush();
        outputStream.close();
        return R.success().set("urlsPath",path);
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
