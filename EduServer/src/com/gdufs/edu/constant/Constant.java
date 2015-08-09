package com.gdufs.edu.constant;
/**
 * 
 * @author zhenghaotao
 * Constant.java
 * 2015年5月10日
 */
public interface Constant {
	
	
	
	//echoCode
	int REGISTER_SUCCESS = 1;
	int REGISTER_REPEAT = 2;
	
	int REGISTERED = 3;
	int NO_REGISTERED = 4;
	
	int PUBLISH_SUCCESS = 5;
	

	int NOT_EXISITED = 6;
	
	int IMAGE_GAIN_SUCCESS = 7;
	
	int QUERY_MY_QUESTIONLIST = 8;
	
	int QUESTION_MUNU_SUCCESS = 9;
	
	int DELE_QUESTION_SUCCESS = 10;
	
	int PUBLISH_COMMENT_SUCCESS = 11;
	
	int GAIN_COMMENT_SUCCESS = 12;
	
	int UPLOAD_SUCCESS = 13;
	
	int UPLOAD_REPEAT = 14;
	
	int QUERY_MY_UPLOADFILES = 15;
	
	int QUERY_UPLOADFILE_LIST = 16;
	
	//问题中附带图片
	String PIC = "1";
	
	//dir
	String ROOT_BASE = "/usr/local/apache-tomcat-7.0.62/webapps/ROOT/";
	String IMG_DIR = "image";
	String FILE_DIR = "files";
	
	//测试用的目录
	String TEMP_BASE = "/home/taotao/";
}
