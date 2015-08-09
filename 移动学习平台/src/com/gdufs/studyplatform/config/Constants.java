package com.gdufs.studyplatform.config;

import android.os.Environment;

public interface Constants {
	String IP = "http://192.168.43.182";
	//问题中附带图片
	String PIC = "1";
	//问题无图
	String NO_PIC = "0";
	
	int TIMEOUT_CONNECTION = 20000;
	int TIMEOUT_SOCKET = 20000;
	int RETRY_TIME = 3;
	//网络异常
	int INTERNET_ERROR = -1;
	
	
	// 返回码
	//登陆成功
	int REGISTER_SUCCESS = 1;
	int REGISTER_REPEAT = 2;
	
	
	int REGISTERED = 3;//注册成功
	
	int NO_REGISTERED = 4;//还没注册
	
	int PUBLISH_SUCCESS = 5;//发表成功
	
	int NOT_EXISITED = 6;//图片已经不再了
	
	@Deprecated
	int IMAGE_GAIN_SUCCESS = 7;//图片获取成功
	
	int MY_QUESTION = 8;//获取我问题列表的成功返回码
	
	int QUESTION_MUNU_SUCCESS = 9;//获取问题列表成功
	
	int DELE_QUESTION_SUCCESS = 10;//删除问题成功
	
	int PUBLISH_COMMENT_SUCCESS = 11;//发表评论成功
	
	int GAIN_COMMENT_SUCCESS = 12;//获取评论成功
	
	int UPLOAD_SUCCESS = 13;//上传成功/
	
	int UPLOAD_REPEAT = 14;//重复,上传失败
	
	int QUERY_MY_UPLOADFILES = 15;//获取我的文件上传列表
	
	int QUERY_UPLOADFILE_LIST = 16;//获取文件上传列表
	
	
	// 服务器地址

	String BASE_URL =  IP + ":8080/EduServer/stu/";

	// 请求
	String REGISTER = BASE_URL + "user_register.do";//用户注册
	String LOGIN = BASE_URL + "user_getUserByImei.do";//用户同imei获取 file用户信息
	String PUBLISH_QUESTION = BASE_URL +  "question_publishQuestion.do";//  用户发布
	
	//抛弃掉
	@Deprecated
	String PUBLISH_GET_IMAGE = BASE_URL +  "question_getImageResource.do";// 用户获取图片资源
	
	
	String PUBLISH_GET_MYQUESTION_LIST = BASE_URL + "question_myQuestionList.do";//获取我的问题列表
	String PUBLISH_GET_QUESTION_LIST = BASE_URL + "question_queryQuestionList.do";//获取所有的问题
	String DELE_QUESTION = BASE_URL + "question_deleQuestion.do";//删除问题
	
	String PUBLISH_COMMENT = BASE_URL + "comment_publishComment.do";//发表评论
	String QUERY_COMMENT_LIST = BASE_URL +  "comment_queryCommentList.do";//获取评论列表
	
	String UPLOAD_FILE = BASE_URL + "resfile_uploadFile.do";//上传文件
	String QUERY_MYUPLOADFILES = BASE_URL + "resfile_queryMyUploadList.do";//查询我的上传列表
	String QUERY_FILELIST = BASE_URL + "resfile_queryFileList.do";//查询所有上传的文件
	
	
	String IP_BASE = IP + ":8080/";
	
	//更新软件才用到的地址
	String UPDATEURL =  IP_BASE + "update.html";//

	// app的文件和图片缓存目录
	String BASE_PATH = Environment.getExternalStorageDirectory().getPath()
			+ "/StudyPlatform";
	String IMAGE_DIR = Environment.getExternalStorageDirectory().getPath()
			+ "/StudyPlatform/imageCache";

	// 数据库
	String APP_NAME = "StudyPlatform";

	// 每页显示的条数
	int PAGE_SIZE = 10;

	// splash界面延时
	int DELAYTIME = 3000;

	// 开发者模式是否开启(开发时用)
	boolean DEVELOPER_MODE = true;

}
