package com.gdufs.studyplatform.bean;

import com.gdufs.studyplatform.base.BaseBean;

public class Question extends BaseBean  {

	private static final long serialVersionUID = 1L;
	
	public static final String TABLE_NAME = "question";
	public static final String ID = "id";
	public static final String USER_ID = "userId";
	public static final String NICKNAME = "nickname";
	public static final String CONTENT = "content";
	public static final String TIMESTAMP = "timestamp";
	public static final String LASTRETIME = "lastretime";
	public static final String  FILE = "file";
	public static final String  FILE_TYPE = "fileType";
	public static final String RECOUNT = "recount";
	
	
	private int userId;
	private String nickname;
	private String content;
	private String timestamp;
	private String lastretime;
	private String file;
	private String fileType;//1有图,0无图
	private int recount;
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	public String getFile() {
		return file;
	}
	public void setFile(String file) {
		this.file = file;
	}
	
	public int getRecount() {
		return recount;
	}
	public void setRecount(int recount) {
		this.recount = recount;
	}
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	public String getLastretime() {
		return lastretime;
	}
	public void setLastretime(String lastretime) {
		this.lastretime = lastretime;
	}
	@Override
	public String toString() {
		return "Question [id=" + id + ", userId=" + userId + ", nickname="
				+ nickname + ", content=" + content + ", timestamp="
				+ timestamp + ", lastretime=" + lastretime + ", file=" + file
				+ ", fileType=" + fileType + ", recount=" + recount + "]";
	}
	
	
	
	

}
