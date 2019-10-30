package com.jtfu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jtfu.entity.Msgbox;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface MsgboxMapper extends BaseMapper<Msgbox> {

    @Select("select count(1) num from msgbox where uid=${uid} and status1=0")
     int getMsgCount(@Param("uid") int uid);
}
