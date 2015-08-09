package com.gdufs.edu.model;

import com.gdufs.edu.base.BaseModel;
/**
 * 
 * @author zhenghaotao
 * Student.java
 * 2015年5月10日
 */
public class User extends BaseModel {

	private static final long serialVersionUID = -8101476785665516926L;
	
	//昵称
	private String nickname;
	//手机的device ID
	private String imei;
	
	private int uploadCount;
	
	private String createTime;
	
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getImei() {
		return imei;
	}
	public void setImei(String imei) {
		this.imei = imei;
	}
	public int getUploadCount() {
		return uploadCount;
	}
	public void setUploadCount(int uploadCount) {
		this.uploadCount = uploadCount;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	@Override
	public String toString() {
		return "User [nickname=" + nickname + ", imei=" + imei
				+ ", uploadCount=" + uploadCount + ", createTime=" + createTime
				+ "]";
	}
	
	
	
	
	
	
}
