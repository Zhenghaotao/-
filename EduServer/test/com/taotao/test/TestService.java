package com.taotao.test;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.gdufs.edu.model.ResFile;
import com.gdufs.edu.model.User;
import com.gdufs.edu.service.ResfileService;
import com.gdufs.edu.service.UserService;

public class TestService {
	
	private static UserService us;
	
	private static ResfileService rs;
	
	@BeforeClass
	public static void iniUserService(){
		ApplicationContext ac = new ClassPathXmlApplicationContext("beans.xml");
		us = (UserService) ac.getBean("userService");
		rs = (ResfileService) ac.getBean("resfileService");
	}
	
	
	@Test
	public void testAddUser(){
		User user = new User();
		user.setImei("fadfafasdfafsd");
		user.setNickname("的马");
		user.setCreateTime("2015-06-07");
		us.saveEntity(user);
		System.out.println(user.getId());
	}
	
	
	@Test
	public void testGetUserBy(){
		User u = us.getEntity(1);
		System.out.println(u);
	}
	
	
	@Test
	public void testDeleteUser(){
		User u = us.getEntity(1);
		us.deleteEntity(u);
	}
	
	
	
}
