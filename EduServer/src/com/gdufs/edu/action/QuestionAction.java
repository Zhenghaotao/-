package com.gdufs.edu.action;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper;
import org.aspectj.util.FileUtil;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.gdufs.edu.base.BaseAction;
import com.gdufs.edu.constant.Constant;
import com.gdufs.edu.model.Page;
import com.gdufs.edu.model.Question;
import com.gdufs.edu.model.Response;
import com.gdufs.edu.service.CommentService;
import com.gdufs.edu.service.QuestionService;
import com.gdufs.edu.util.CommonUtil;
import com.gdufs.edu.util.ResponseUtil;
@Controller
@Scope("prototype")
public class QuestionAction extends BaseAction<Question> {

	private static final long serialVersionUID = 1L;
	private String fileFileName;
	private String fileContentType;
	
	/**
	 * 图片路径
	 */
	private String fileUrl;

	public String getFileUrl() {
		return fileUrl;
	}

	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}
	
	public String getFileContentType() {
		return fileContentType;
	}

	public void setFileContentType(String fileContentType) {
		this.fileContentType = fileContentType;
	}

	public String getFileFileName() {
		return fileFileName;
	}

	public void setFileFileName(String fileFileName) {
		this.fileFileName = fileFileName;
	}

	@Resource
	private QuestionService questionService;
	
	@Resource
	private CommentService commentService;
	
	public String publishQuestion(){
		Response res = new Response();
		Question question = model;
		question.setLastretime(String.valueOf(new Date().getTime()));
		question.setTimestamp(String.valueOf(new Date().getTime()));
		question.setRecount(0);
		if(ServletActionContext.getRequest() instanceof MultiPartRequestWrapper){
			MultiPartRequestWrapper pr = (MultiPartRequestWrapper)ServletActionContext.getRequest();
			if(pr.getFiles("file") != null && pr.getFiles("file").length > 0){
				String uuid=UUID.randomUUID().toString().replaceAll("-", "");
				File file = pr.getFiles("file")[0];
				String dir = Constant.ROOT_BASE + Constant.IMG_DIR;
				File des = new File(dir + "/" + uuid);
				try {
					FileUtil.copyFile(file, des);
					question.setFile(Constant.IMG_DIR + "/" + uuid);
					System.out.println("fileFileName == " + fileFileName);
					System.out.println("fileContentType == " + fileContentType);
				} catch (IOException e) {
					System.out.println(e.getMessage());
				}
			}
		}
		questionService.saveEntity(question);
		res.setQuestion(question);
		res.setEchoCode(Constant.PUBLISH_SUCCESS);
		ResponseUtil.responseToUser(getResponse(), res);
		return null;
	}
	
	public String queryQuestionList(){
		Response res = new Response();
		List<Question> questions = questionService.queryQuestionList();
		res.setEchoCode(Constant.QUESTION_MUNU_SUCCESS);
		res.setQuestionList(questions);
		ResponseUtil.responseToUser(getResponse(), res);
		
		return null;
	}
	
	public String deleQuestion(){
		Question question = model;
		int q_id = question.getId();
		
		Question q = questionService.getEntity(q_id);
		commentService.deleCommentsByQuestionId(q_id);
		//删除图片
		if(Constant.PIC.equals(q.getFileType())){
			String img = Constant.ROOT_BASE + q.getFile();
			File imgFile = new File(img);
			if(imgFile.exists()){
				imgFile.delete();
			}
		}
		
		//删除对应的评论
		
		Response res = new Response();
		
		questionService.deleteEntity(q);
		res.setEchoCode(Constant.DELE_QUESTION_SUCCESS);
		ResponseUtil.responseToUser(getResponse(), res);
		
		return null;
	}
	
	
	public String myQuestionList(){
		System.out.println("开始了-----myQuestionList");
		Response res = new Response();
		Question question = model;
		int userId  = question.getUserId();
		List<Question> quesList = questionService.querySelfQuestionList(userId);
		res.setEchoCode(Constant.QUERY_MY_QUESTIONLIST);
		res.setQuestionList(quesList);
		ResponseUtil.responseToUser(getResponse(), res);
		return null;
	}
	public String questionList(){
		
		Response res = new Response();
		page = questionService.queryQuestionListByPage(page);
		return null;
	}
	private Page page;

	public Page getPage() {
		return page;
	}

	public void setPage(Page page) {
		this.page = page;
	}
}
