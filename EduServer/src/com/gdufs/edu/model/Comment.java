package com.gdufs.edu.model;

import com.gdufs.edu.base.BaseModel;

public class Comment extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int questionId;
	private int userId;
	private String content;
	private String nickname;
	private String rank;
	private String timestamp;
	public int getQuestionId() {
		return questionId;
	}
	public void setQuestionId(int questionId) {
		this.questionId = questionId;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getRank() {
		return rank;
	}
	public void setRank(String rank) {
		this.rank = rank;
	}
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	@Override
	public String toString() {
		return "Comment [questionId=" + questionId + ", userId=" + userId
				+ ", content=" + content + ", nickname=" + nickname + ", rank="
				+ rank + ", timestamp=" + timestamp + "]";
	}
}
