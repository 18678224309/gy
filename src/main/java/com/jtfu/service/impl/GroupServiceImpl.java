package com.jtfu.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.jtfu.entity.Group1;
import com.jtfu.mapper.GroupMapper;
import com.jtfu.service.IGroupService;
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
public class GroupServiceImpl extends ServiceImpl<GroupMapper, Group1> implements IGroupService {

}
