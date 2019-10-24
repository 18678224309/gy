package com.jtfu.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jtfu.entity.Comment;
import com.jtfu.mapper.CommentMapper;
import com.jtfu.service.ICommentService;
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
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements ICommentService {

}
