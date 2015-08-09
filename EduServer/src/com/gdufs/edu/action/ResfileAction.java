package com.gdufs.edu.action;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper;
import org.aspectj.util.FileUtil;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.gdufs.edu.base.BaseAction;
import com.gdufs.edu.constant.Constant;
import com.gdufs.edu.model.ResFile;
import com.gdufs.edu.model.Response;
import com.gdufs.edu.model.User;
import com.gdufs.edu.service.ResfileService;
import com.gdufs.edu.service.UserService;
import com.gdufs.edu.util.ResponseUtil;


@Controller
@Scope("prototype")
public class ResfileAction extends BaseAction<ResFile> {

	private static final long serialVersionUID = 1L;
	
	
	@Resource
	private ResfileService resfileService;
	
	@Resource
	private UserService userService;
	
	private String fileFileName;
	private String fileContentType;
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
	public String queryMyUploadList(){
		ResFile resFile = model;
		Response res = new Response();
		List<ResFile> list = resfileService.findEntityByHQL("FROM ResFile r WHERE r.nickname=?  ORDER BY r.id desc", resFile.getNickname());
		
		res.setResfileList(list);
		res.setEchoCode(Constant.QUERY_MY_UPLOADFILES);
		ResponseUtil.responseToUser(getResponse(), res);
		return null;
	}
	
	public String queryFileList(){
		Response res = new Response();
		List<ResFile> list = resfileService.findEntityByHQL("FROM ResFile r ORDER BY r.id desc");
		
		res.setResfileList(list);
		res.setEchoCode(Constant.QUERY_UPLOADFILE_LIST);
		ResponseUtil.responseToUser(getResponse(), res);
		return null;
	}
	
	/**
	 * 上传文件
	 * @return
	 */
	public String uploadFile(){
		
		Response res = new Response();
		ResFile resFile = model;
		
		ResFile existed = resfileService.findFileByNickInstance(resFile);
		//说明文件重复
		System.out.println(existed);
		if(existed != null){
			res.setEchoCode(Constant.UPLOAD_REPEAT);
			ResponseUtil.responseToUser(getResponse(), res);
			return null;
		}
		
		if(ServletActionContext.getRequest() instanceof MultiPartRequestWrapper){
			MultiPartRequestWrapper pr = (MultiPartRequestWrapper)ServletActionContext.getRequest();
			if(pr.getFiles("file") != null && pr.getFiles("file").length > 0){
				File file = pr.getFiles("file")[0];
				String dir = Constant.ROOT_BASE + Constant.FILE_DIR;
			    String path = ServletActionContext.getServletContext().getRealPath(dir);
				File des = new File(dir + "/" + resFile.getFilename());
				resFile.setUrl(Constant.FILE_DIR + "/" + resFile.getFilename());
				resFile.setTime(String.valueOf(new Date().getTime()));
				User user = userService.findUserByNickname(resFile.getNickname());
				user.setUploadCount(user.getUploadCount() + 1);
				userService.updateEntity(user);
				
				try {
					FileUtil.copyFile(file, des);
					System.out.println("fileFileName == " + fileFileName);
					System.out.println("fileContentType == " + fileContentType);
				} catch (IOException e) {
					System.out.println(e.getMessage());
				}
			}
			resfileService.saveEntity(resFile);
			res.setEchoCode(Constant.UPLOAD_SUCCESS);
			ResponseUtil.responseToUser(getResponse(), res);
		}
		return null;
	}

}
