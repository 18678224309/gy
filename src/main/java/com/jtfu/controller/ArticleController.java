package com.jtfu.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jtfu.entity.Article;
import com.jtfu.entity.Message;
import com.jtfu.entity.User;
import com.jtfu.mapper.ArticleMapper;
import com.jtfu.mapper.MessageMapper;
import com.jtfu.mapper.UserMapper;
import com.jtfu.service.IArticleService;
import com.jtfu.util.R;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.lang.reflect.Field;
import java.util.*;


@Controller
@RequestMapping("/article")
public class ArticleController {

    @Autowired
    ArticleMapper articleMapper;
    @Autowired
    MessageMapper messageMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    FileSystem fsSource;
    @Value("${imageServer}")
    String imageServer;

    @PostMapping("/uoloadImg")
    @ResponseBody
    public R uoloadImg(@RequestPart("photos") MultipartFile photos,@RequestParam("url") String url, HttpServletRequest request) throws Exception {
        //当图片上传时会利用拿到的唯一key值作为文件夹，上传的图片存在这个文件夹中
        String pathName=url+"/"+UUID.randomUUID().toString()+".jpg";
        System.err.println(pathName);
        File file=new File(pathName);
        OutputStream outputStream=new FileOutputStream(file);
        outputStream.write(photos.getBytes());
        outputStream.flush();
        outputStream.close();
        return R.success();
    }


    @GetMapping("/reply.html")
    public String replyHtml(@RequestParam("articleId")int articleId, Model model, HttpSession session){
        //依据文章id，查询到文章内容
        Article article=articleMapper.selectById(articleId);
        User auth=userMapper.selectById(article.getAuthId());
        QueryWrapper queryWrapper=new QueryWrapper();
        queryWrapper.eq("articleid",articleId);
        List<Message> messages=messageMapper.selectList(queryWrapper);
        //将图片进行拆分
        article.setPhotoPath(article.getPhotos().split(","));
        //循环回复信息，拿到回复的用户信息
        for(int i=0;i<messages.size();i++){
            Message message= messages.get(i);
            int userId= message.getReplyid();
            User user=userMapper.selectById(userId);
            message.setUser(user);
            messages.set(i,message);
        }
        User user= (User) session.getAttribute("userInfo");
        //判断当前登录的用户是否与帖子的作者相同,是：更新帖子的更新时间
        if(user.getId().equals(article.getAuthId())){
            article.setUpdatetime(new Date());
            articleMapper.updateById(article);
        }
        model.addAttribute("userInfo",user);
        model.addAttribute("model",article);
        model.addAttribute("messages",messages);
        model.addAttribute("auth",auth);
        return "reply";
    }

    @PostMapping("/genKey")
    @ResponseBody
    public R genKey(HttpServletRequest request){
        //在前端进行图片上传时，选定图片就已经上传了，此时需要与正在输入的数据绑定，所以返回一个唯一的key值；
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
    public R saveArticle(String title,String describe,int money,String urlPath,HttpSession session) throws IOException {
        User user= (User) session.getAttribute("userInfo");
        //保存数据接口，接收标题，描述，筹集金额，上传图片的地址;
        Article article=new Article();
        article.setTitle(title);
        article.setAuth(user.getName());
        article.setAuthId(user.getId());
        article.setMoney(money);
        article.setDescribe(describe);
        File file=new File(urlPath);
        File[] files=file.listFiles();
        StringBuilder builder=new StringBuilder();
        int index= urlPath.indexOf("\\static");
       if(files!=null){
           String imgPath=urlPath.substring(index,urlPath.length());
           //hadoop创建节点
           String hadoopDir="/gyImg/"+imgPath.substring(14);
           fsSource.mkdirs(new Path(hadoopDir));
           for(int i=0;i<files.length;i++){
               File f=files[i];
               String copyPath=urlPath+"/"+f.getName();
               String hadoopPath=hadoopDir+"/"+f.getName();
               builder.append(imageServer+"/webhdfs/v1"+hadoopPath+"?op=open&user.name=root");
               builder.append(",");
               //复制文件到hadoop
               System.out.println("copyPath:"+copyPath);
               System.out.println("hadoopDir:"+hadoopDir);
               fsSource.copyFromLocalFile(new Path(copyPath),new Path(hadoopDir));
           }
       }
        article.setPhotos(builder.toString());
        article.setCreatetime(new Date());
        article.setStatus(-1);
        articleMapper.insert(article);
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
        int authId=0;
        if(map.containsKey("authId")){authId= Integer.valueOf((String) map.get("authId"));}
        List<Article> list=articleMapper.getArticleListPage((page.getCurrent()-1)*page.getSize(),page.getSize(),"createtime");
        page.setRecords(list);
        page.setTotal(articleMapper.selectCount(queryWrapper));
        return R.success().set("page",page);
    }

    @PostMapping("/getAudit")
    @ResponseBody
    public R getAudit(@RequestParam Map map){
        Field[] fields= Article.class.getDeclaredFields();
        QueryWrapper queryWrapper=new QueryWrapper();
        Page page=new Page();
        page.setCurrent(Long.valueOf(map.get("curr").toString()));
        page.setSize(Long.valueOf( map.get("limit").toString()));

        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            String fieldName = field.getName();
            if (map.containsKey(fieldName)) {
                queryWrapper.eq(fieldName, map.get(fieldName));
            }
        }
        return R.success().set("page",articleMapper.selectPage(page,queryWrapper));
    }

    @PostMapping("/getHot")
    @ResponseBody
    public R getHot(){
        List<Article> list=articleMapper.getArticleListPage(0,20,"replyNum");
        return R.success().set("hotList",list);
    }

    @PostMapping("/getSlideShow")
    @ResponseBody
    public R getSlideShow(){
        //获取轮播图，取前五条数据的第一张图片作为轮播图片;
        QueryWrapper wrapper=new QueryWrapper();
        wrapper.eq("status",1);
        wrapper.isNotNull("photos");
        wrapper.ne("photos","");
        wrapper.orderByAsc("createtime");
        List<Article> list= articleMapper.selectList(wrapper);
        if(list.size()>5){
            list=list.subList(0,5);
        }

        for (int i=0;i<list.size();i++){
            Article article=list.get(i);
            String photos= article.getPhotos();
            String[] photo=photos.split(",");
            article.setPhotos(photo[0]);
            list.set(i,article);
        }

        return R.success().set("SlideShow",list);
    }

    @PostMapping("/updateArticle")
    @ResponseBody
    public R updateArticle(@RequestParam Map map){
        int id=Integer.valueOf(map.get("id").toString());
        Article article= articleMapper.selectById(id);
        if(article!=null&&map.get("status")!=null){
            article.setStatus(Integer.valueOf(map.get("status").toString()));
        }
        if(article!=null&&map.get("money")!=null){
           int money=Integer.valueOf(map.get("money").toString());
           if(money>article.getMoney()-article.getMoneynow()){
               return R.success("捐献失败！捐献金额大于所需金额！"+(article.getMoney()-article.getMoneynow()));
           }
           article.setMoneynow(article.getMoneynow()+money);//将当前金额进行更新;
            if(article.getMoneynow().equals(article.getMoney())){
                article.setStatus(0);
            }
        }
        articleMapper.updateById(article);
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
