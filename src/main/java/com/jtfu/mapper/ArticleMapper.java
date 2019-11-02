package com.jtfu.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jtfu.entity.Article;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author jtfu
 * @since 2019-10-24
 */
public interface ArticleMapper extends BaseMapper<Article> {

    @Select("select a.id,title,auth,authId,money,describes,photos,createtime,status,moneynow,IFNULL(b.count,0) replyNum from article a\n" +
            "left join (select articleid,count(1) count from message GROUP BY articleid) b on a.id=b.articleid where a.status=1 or a.status=0 order by ${order} desc limit ${begin},${end} ")
     List<Article> getArticleListPage(@Param("begin")long begin,@Param("end")long end,@Param("order")String order);

}
