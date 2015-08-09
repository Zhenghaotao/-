package com.gdufs.edu.model;

import com.gdufs.edu.base.BaseModel;
/**
 * 
 * @author zhenghaotao
 * Question.java
 * 2015年5月10日
 */
public class Question extends BaseModel {

	private static final long serialVersionUID = -8308026732975152728L;
	
	
	private int userId;
	private String nickname;
	//问题内容
	private String content;
	private String timestamp;
	private String lastretime;
	private String file;
	private String fileType;
	private int recount;
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getContent() {
		return content;
	}
	
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
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
	public String getLastretime() {
		return lastretime;
	}
	public void setLastretime(String lastretime) {
		this.lastretime = lastretime;
	}
	public String getFile() {
		return file;
	}
	public void setFile(String file) {
		this.file = file;
	}
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	public int getRecount() {
		return recount;
	}
	public void setRecount(int recount) {
		this.recount = recount;
	}
	@Override
	public String toString() {
		return "Question [userId=" + userId + ", nickname=" + nickname
				+ ", content=" + content + ", timestamp=" + timestamp
				+ ", lastretime=" + lastretime + ", file=" + file
				+ ", fileType=" + fileType + ", recount=" + recount + "]";
	}
	
	
}
