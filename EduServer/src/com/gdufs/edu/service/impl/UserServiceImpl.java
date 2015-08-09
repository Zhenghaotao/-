package com.gdufs.edu.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.gdufs.edu.base.BaseDao;
import com.gdufs.edu.base.impl.BaseServiceImpl;
import com.gdufs.edu.model.User;
import com.gdufs.edu.service.UserService;
@Service("userService")
public class UserServiceImpl extends BaseServiceImpl<User> implements
		UserService {
	/**
	 * 重写该方法,目的是为了覆盖超类中该方法的注解,指明注入指定的Dao对象,否则spring
	 * 无法确定注入哪个Dao---有四个满足条件的Dao.
	 */
	@Resource(name="userDao")
	public void setDao(BaseDao<User> dao) {
		super.setDao(dao);
	}

	@Override
	public User findUserByImei(String imei) {
		User u =  (User) dao.uniqueResult("from User u where u.imei = ?", imei);
		return u;
	}

	@Override
	public User findUserByNickname(String nickname) {
		User u =  (User) dao.uniqueResult("from User u where u.nickname = ?", nickname);
		return u;
	}
	
	
}
