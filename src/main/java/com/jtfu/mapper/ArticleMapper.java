package com.jtfu.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
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

     IPage<Article> getArticleListPage(IPage page,@Param("art") Article article,@Param("orderBy") String orderBy);

}
