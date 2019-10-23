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
    public R uoloadImg(@RequestPart("photos") MultipartFile photos, String url, HttpServletRequest request) throws Exception {
        String staticPath=request.getRealPath("static");
        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
        String path=staticPath+"\\image\\"+dateFormat.format(new Date());
        File file=new File(path);
        if(!file.exists()){
            file.mkdir();
        }
        file=new File(path+"/"+ UUID.randomUUID().toString()+".jpg");
        OutputStream outputStream=new FileOutputStream(file);
        outputStream.write(photos.getBytes());
        outputStream.flush();
        outputStream.close();

        file=new File(path);
        List list=new ArrayList();
        if(file.exists()&&file.isDirectory()){
            String[] fileNames= file.list();
            Arrays.stream(fileNames).forEach(s->{
                list.add(s);
            });

        }
        return R.success().set("urls",list);
    }
}
