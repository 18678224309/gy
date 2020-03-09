package com.jtfu.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jtfu.entity.Article;
import com.jtfu.entity.Artrecord;
import com.jtfu.entity.Message;
import com.jtfu.entity.User;
import com.jtfu.mapper.ArticleMapper;
import com.jtfu.mapper.ArtrecordMapper;
import com.jtfu.mapper.MessageMapper;
import com.jtfu.mapper.UserMapper;
import com.jtfu.service.IArticleService;
import com.jtfu.util.R;
import com.jtfu.util.ServletUtils;
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
    ArtrecordMapper artrecordMapper;

    private Map<String,List> photoMaps=new HashMap<>();

    @PostMapping("/uoloadImg")
    @ResponseBody
    public R uoloadImg(@RequestPart("photos") MultipartFile photos,@RequestParam("url") String url) throws Exception {
        List list= photoMaps.get(url);
        //当图片上传时会利用拿到的唯一key值作为文件夹，上传的图片存在这个文件夹中
        String pathName=url+"/"+UUID.randomUUID().toString()+".jpg";
        if(list!=null){
            list.add(pathName);
        }else{
            list=new ArrayList();
            list.add(pathName);
            photoMaps.put(url,list);
        }
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
        String staticPath=request.getRealPath("/");
        String path=staticPath+"/static/image/"+uuid;
        File file=new File(path);
        if(!file.exists()){
            file.mkdir();
        }
        return R.success().set("key",path);
    }

    @PostMapping("/saveArticle")
    @ResponseBody
    public R saveArticle(Article article){
        HttpSession session = ServletUtils.getRequest().getSession();
        User user= (User) session.getAttribute("userInfo");
        //保存数据接口，接收标题，描述，筹集金额，上传图片的地址;
        article.setAuth(user.getName());
        article.setAuthId(user.getId());
        String urlPath = article.getUrlPath();
        if(photoMaps.containsKey(urlPath)){
            StringBuilder builder=new StringBuilder();
            List<String> list=photoMaps.get(urlPath);
            for(int i=0;i<list.size();i++){
                String path=list.get(i);
                System.out.println(path+"================Path");
                int index= path.indexOf("/static");
                String copyPath=path.substring(index);
                builder.append(copyPath);
                builder.append(",");
            }
            article.setPhotos(builder.toString());
            photoMaps.remove(urlPath);
        }

        article.setCreatetime(new Date());
        article.setStatus(-1);//-1待审核
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
    public R getList(Article article,int curr,int limit){
       IPage page=new Page();
       page.setCurrent(curr);
       page.setSize(limit);
       page = articleMapper.getArticleListPage(page, article,null);
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
            Field field= fields[i];
            String fieldName=field.getName();
            if(map.containsKey(fieldName)){
                queryWrapper.eq(fieldName,map.get(fieldName));
            }
        }
        return R.success().set("page",articleMapper.selectPage(page,queryWrapper));
    }

    @PostMapping("/getHot")
    @ResponseBody
    public R getHot(){
        IPage page=new Page();
        page.setCurrent(1);
        page.setSize(20);
        IPage<Article> HOT = articleMapper.getArticleListPage(page, new Article(), "replyNum");
        return R.success().set("page",HOT);
    }

    @PostMapping("/deleteArticle")
    @ResponseBody
    public R deleteArticle(int id){
        Article article= articleMapper.selectById(id);
        if(article!=null&&article.getMoneynow()==0){
            articleMapper.deleteById(id);
            return R.success();
        }else if(article!=null&&article.getMoneynow()>0){
            return R.error("无法删除！已经发起筹集金额："+article.getMoneynow());
        }
        return R.error();
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
        HttpSession session = ServletUtils.getSession();
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
            User userInfo = (User) session.getAttribute("userInfo");
            Artrecord artrecord=new Artrecord();
            artrecord.setArticleid(article.getId());
            artrecord.setCreatetime(new Date());
            artrecord.setMoney(money);
            artrecord.setName(userInfo.getName());
            artrecord.setUserid(userInfo.getId());
            artrecordMapper.insert(artrecord);
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
