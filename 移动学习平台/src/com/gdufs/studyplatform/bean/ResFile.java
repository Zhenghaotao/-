package com.gdufs.studyplatform.bean;

import com.gdufs.studyplatform.base.BaseBean;

public class ResFile extends BaseBean {

	public static final long serialVersionUID = 1L;
	public static final String TABLE_NAME = "resfile";
	public static final String ID = "id";
	public static final String NICKNAME = "nickname";
	public static final String FILENAME = "filename";
	public static final String URL = "url";
	public static final String TIME = "time";
	
	private String nickname;
	private String filename;
	private String url;
	private String time;
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	@Override
	public String toString() {
		return "ResFile [id=" + id + ", nickname=" + nickname + ", filename="
				+ filename + ", url=" + url + ", time=" + time + "]";
	}
	
}
