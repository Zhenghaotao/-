package com.gdufs.studyplatform.bean;

import java.io.Serializable;

import com.gdufs.studyplatform.base.BaseBean;

public class User extends BaseBean  {

	private static final long serialVersionUID = 1L;
	
	
	public static final String TABLE_NAME = "user";
	public static final String ID = "id";
	public static final String NICKNAME = "nickname";
	public static final String IMEI = "imei";
	public static final String UPLOADCOUNT = "uploadCount";
	public static final String CREATETIME = "createTime";
	
	
	private String nickname;
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
		return "User [id=" + id + ", nickname=" + nickname + ", imei=" + imei
				+ ", uploadCount=" + uploadCount + ", createTime=" + createTime
				+ "]";
	}
}
