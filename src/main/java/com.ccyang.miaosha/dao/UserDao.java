package com.ccyang.miaosha.dao;

import com.ccyang.miaosha.domain.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * 运行时会动态生成接口的代理对象，不需要@Repository 注解
 */
@Mapper
@Repository  // 此注解可有可无
public interface UserDao {

    @Select("select * from user where id = #{id}")
    User getById(@Param("id") int id);

    @Insert("insert into user(id,name) values(#{id},#{name})")
    int insert(User user);

}


