package com.jtfu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jtfu.entity.Msgbox;
import com.jtfu.mapper.MsgboxMapper;
import com.jtfu.service.IMsgBoxService;
import org.springframework.stereotype.Service;

@Service
public class MsgboxServiceImpl extends ServiceImpl<MsgboxMapper, Msgbox> implements IMsgBoxService {
}
