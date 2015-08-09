package com.gdufs.edu.action;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.gdufs.edu.base.BaseAction;
import com.gdufs.edu.constant.Constant;
import com.gdufs.edu.model.Comment;
import com.gdufs.edu.model.Question;
import com.gdufs.edu.model.Response;
import com.gdufs.edu.service.CommentService;
import com.gdufs.edu.service.QuestionService;
import com.gdufs.edu.util.ResponseUtil;
@Controller
@Scope("prototype")
public class CommentAction extends BaseAction<Comment> {

	private static final long serialVersionUID = 1L;
	
	@Resource
	private CommentService commentService;
	
	@Resource
	private QuestionService questionService;
	/**
	 * 发表评论
	 * @return
	 */
	public String publishComment(){
		Comment comment = model;
		
		comment.setTimestamp(String.valueOf(new Date().getTime()));
		commentService.saveEntity(comment);
		Question question = questionService.getEntity(comment.getQuestionId());
		question.setLastretime(String.valueOf(new Date().getTime()));
		question.setRecount(question.getRecount() + 1);
		questionService.updateEntity(question);
		Response res = new Response();
		res.setEchoCode(Constant.PUBLISH_COMMENT_SUCCESS);
		ResponseUtil.responseToUser(getResponse(), res);
		
		return null;
	}
	
	public String queryCommentList(){
		System.out.println("queryCommentList");
		Comment comment = model;
		int q_id = comment.getQuestionId();
		List<Comment> list = commentService.queryCommentsByQid(q_id);
		Response res = new Response();
		res.setEchoCode(Constant.GAIN_COMMENT_SUCCESS);
		res.setCommentList(list);
		ResponseUtil.responseToUser(getResponse(), res);
		return null;
	}
	
	
	
}
