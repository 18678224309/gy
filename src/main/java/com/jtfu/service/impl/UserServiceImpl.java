package com.jtfu.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jtfu.entity.User;
import com.jtfu.mapper.UserMapper;
import com.jtfu.service.IUserService;
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
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

}
