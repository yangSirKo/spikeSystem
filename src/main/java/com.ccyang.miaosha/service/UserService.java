package com.ccyang.miaosha.service;

import com.ccyang.miaosha.dao.UserDao;
import com.ccyang.miaosha.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    /**
     * userDao (Proxy)对象会在 mybatis运行时进行创建并注入
     */
    @Autowired
    private UserDao userDao;

    public User getById(int id){
        return userDao.getById(id);
    }

    /**
     * 测试事务是否起作用，结果是起作用
     */
    @Transactional
    public boolean tx(){
        User user = new User();
        user.setId(2);
        user.setName("wuSong");
        userDao.insert(user);

        // id=1 的记录表中已存在，有事务保证的话，这两条插入记录都会被回滚。
        User user2 = new User();
        user.setId(1);
        user.setName("liSong");
        userDao.insert(user2);
        return true;
    }

}
