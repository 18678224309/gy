package com.jtfu.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jtfu.entity.Article;
import com.jtfu.mapper.ArticleMapper;
import com.jtfu.service.IArticleService;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author jtfu
 * @since 2019-10-24
 */
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements IArticleService {

}
