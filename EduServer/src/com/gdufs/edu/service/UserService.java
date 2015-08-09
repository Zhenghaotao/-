package com.gdufs.edu.service;

import com.gdufs.edu.base.BaseService;
import com.gdufs.edu.model.User;

public interface UserService extends BaseService<User> {

	User findUserByImei(String imei);

	User findUserByNickname(String nickname);

}
