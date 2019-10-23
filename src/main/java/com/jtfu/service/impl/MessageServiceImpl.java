package com.jtfu.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jtfu.entity.Message;
import com.jtfu.mapper.MessageMapper;
import com.jtfu.service.IMessageService;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author jtfu
 * @since 2019-10-22
 */
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements IMessageService {

}
