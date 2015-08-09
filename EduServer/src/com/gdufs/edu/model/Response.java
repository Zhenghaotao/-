package com.gdufs.edu.model;

import java.io.Serializable;
import java.util.List;

public class Response implements Serializable {

	private static final long serialVersionUID = 1L;
	
	
	private int echoCode  = 0;
	private User user;
	private ResFile resFile;
	private Question question;
	private Comment comment;
	private Page page;
	private String message;
	private List<Question> questionList;
	private List<Comment> commentList;
	private List<ResFile> resfileList;
	public int getEchoCode() {
		return echoCode;
	}
	public void setEchoCode(int echoCode) {
		this.echoCode = echoCode;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	
	public ResFile getResFile() {
		return resFile;
	}
	public void setResFile(ResFile resFile) {
		this.resFile = resFile;
	}
	public Question getQuestion() {
		return question;
	}
	public void setQuestion(Question question) {
		this.question = question;
	}
	public Comment getComment() {
		return comment;
	}
	public void setComment(Comment comment) {
		this.comment = comment;
	}
	public Page getPage() {
		return page;
	}
	public void setPage(Page page) {
		this.page = page;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public List<Question> getQuestionList() {
		return questionList;
	}
	public void setQuestionList(List<Question> questionList) {
		this.questionList = questionList;
	}
	public List<Comment> getCommentList() {
		return commentList;
	}
	public void setCommentList(List<Comment> commentList) {
		this.commentList = commentList;
	}
	public List<ResFile> getResfileList() {
		return resfileList;
	}
	public void setResfileList(List<ResFile> resfileList) {
		this.resfileList = resfileList;
	}
	
	
}
