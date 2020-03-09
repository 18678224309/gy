package com.jtfu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jtfu.entity.Artrecord;
import com.jtfu.mapper.ArtrecordMapper;
import com.jtfu.service.IArtrecordService;
import org.springframework.stereotype.Service;

@Service
public class ArtrecodeServiceImpl extends ServiceImpl<ArtrecordMapper, Artrecord> implements IArtrecordService {
}
