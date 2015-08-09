package com.gdufs.edu.action;

import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.gdufs.edu.base.BaseAction;
import com.gdufs.edu.constant.Constant;
import com.gdufs.edu.model.Response;
import com.gdufs.edu.model.User;
import com.gdufs.edu.service.UserService;
import com.gdufs.edu.util.ResponseUtil;
import com.gdufs.edu.util.TimeUtil;

@Controller
@Scope("prototype")
public class UserAction extends BaseAction<User> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Resource
	private UserService userService;

	public String register() {
		Response res = new Response();
		User user = model;
		String nickname = user.getNickname();
		User info = userService.findUserByNickname(nickname);
		if (info != null) {
			res.setEchoCode(Constant.REGISTER_REPEAT);
		} else {
			user.setUploadCount(0);
			user.setCreateTime(TimeUtil.getPreciseTimer());
			userService.saveEntity(user);
			res.setUser(model);
			res.setEchoCode(Constant.REGISTER_SUCCESS);
			System.out.println("register success !!!!");
		}
		ResponseUtil.responseToUser(getResponse(), res);

		return null;
	}
	/**
	 * 根据用户手机的imei获取用户信息
	 * @return
	 */
	public String getUserByImei(){
		Response res = new Response();
		User user = model;
		String iemi = user.getImei();
		User info  = userService.findUserByImei(iemi);
		if(info == null){
			res.setEchoCode(Constant.NO_REGISTERED);
		} else {
			res.setEchoCode(Constant.REGISTERED);
			res.setUser(info);
		}
		ResponseUtil.responseToUser(getResponse(), res);
		return null;
	}
	

}
