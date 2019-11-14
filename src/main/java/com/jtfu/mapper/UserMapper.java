package com.jtfu.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jtfu.entity.User;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author jtfu
 * @since 2019-10-22
 */
public interface UserMapper extends BaseMapper<User> {

    @Select("select  a.id,a.username,a.name,a.avatar,IFNULL(b.count,0) replyNum from `user` a\n" +
            "left join (select replyid,count(1) count from message GROUP BY replyid) b on a.id=b.replyid ORDER BY replyNum desc LIMIT 0,30 ")
    List<User> getHotUser();



}
