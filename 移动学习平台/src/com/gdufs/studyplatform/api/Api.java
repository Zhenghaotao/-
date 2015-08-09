package com.gdufs.studyplatform.api;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

import com.gdufs.studyplatform.bean.Comment;
import com.gdufs.studyplatform.bean.Question;
import com.gdufs.studyplatform.bean.ResFile;
import com.gdufs.studyplatform.bean.Response;
import com.gdufs.studyplatform.bean.User;
import com.gdufs.studyplatform.config.Constants;
import com.gdufs.studyplatform.util.LogUtils;
import com.google.gson.Gson;

/**
 * API客户端接口：用于访问网络数据
 * @author root
 *
 */
public class Api {
	private static final String TAG = "Api";

	public static String httpPost(String url,Map<String,String> map) throws ClientProtocolException, IOException{
		return httpPost(url,map,null);
	}
	/**
	 * 通用接口到服务器
	 * @param url
	 * @param map
	 * @param file
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static String httpPost(String url,Map<String,String> map,File file) throws ClientProtocolException, IOException
	{
		 HttpPost httpPost = new HttpPost(url);  
         // 设置字符集  
		 MultipartEntity mpEntity = new MultipartEntity(); //文件传输
		 if(file!=null){
			 ContentBody cbFile = new FileBody(file);
			 mpEntity.addPart("file", cbFile);
		 }
		 for(String key:map.keySet())
		 {
			 StringBody stringBody = new StringBody(map.get(key).toString(),
                     Charset.forName("UTF-8"));
			 mpEntity.addPart(key,stringBody);
		 }
		 
         // 设置参数实体  
         httpPost.setEntity(mpEntity);  
         LogUtils.i(TAG,"request params:--->>" + map.toString());
         // 获取HttpClient对象  
         HttpClient httpClient = new DefaultHttpClient();  
         //连接超时  
         httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);  
         //请求超时  
         httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 5000);  
 
         HttpResponse httpResp = httpClient.execute(httpPost);  
         String json = EntityUtils.toString(httpResp.getEntity(), "UTF-8");
         return json;
	}
	/**
	 * 通过手机imei获取用户信息
	 * @param imei
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static Response getUserByImei(String imei) throws ClientProtocolException, IOException {
		Map<String,String> userMap = new HashMap<String, String>();
		userMap.put("imei", imei);
		String json = httpPost(Constants.LOGIN,userMap);
		Response response = new Gson().fromJson(json, Response.class);
    	return response;
	}

	
	
	/**
	 * 用户注册
	 * @param user
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static Response userRegister(User user) throws ClientProtocolException, IOException {
		Map<String,String> userMap = new HashMap<String,String>();
		userMap.put("imei", user.getImei());
		userMap.put("nickname", user.getNickname());
		String json = httpPost(Constants.REGISTER,userMap);
		LogUtils.i(TAG, json);
		Response response = new Gson().fromJson(json, Response.class);
		return response;
	}
	/**
	 * 上传文件到服务器
	 * @param resfile
	 * @param file
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static Response uploadFile(ResFile resfile,File file) throws ClientProtocolException, IOException {
		Map<String,String> fileMap = new HashMap<String, String>();
		fileMap.put("filename", resfile.getFilename());
		fileMap.put("nickname", resfile.getNickname());
		String json = null;
		json = httpPost(Constants.UPLOAD_FILE, fileMap, file);
		LogUtils.i(TAG, json);
		Response response = new Gson().fromJson(json, Response.class);
		return response;
	}

	/**
	 * 发布问题到服务器上
	 * @param question
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static Response publicQuestion(Question question) throws ClientProtocolException, IOException {
		Map<String,String> quesMap = new HashMap<String, String>();
		quesMap.put("userId", String.valueOf(question.getUserId()));
		quesMap.put("content", question.getContent());
		quesMap.put("nickname", question.getNickname());
		quesMap.put("fileType", question.getFileType());
		String json = null;
		if(question.getFile() != null){
			json = httpPost(Constants.PUBLISH_QUESTION, quesMap, new File(question.getFile()));
		} else {
			json = httpPost(Constants.PUBLISH_QUESTION, quesMap);
		}
		LogUtils.i(TAG, json);
		Response response = new Gson().fromJson(json, Response.class);
		return response;
	}
	
	
	/**
	 * 获取图片资源
	 * @param fileUrl
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	@Deprecated
	public static Response getImageResource(String fileUrl) throws ClientProtocolException, IOException{
		Map<String,String> quesMap = new HashMap<String, String>();
		quesMap.put("fileUrl", fileUrl);
		String json = null;
		json = httpPost(Constants.PUBLISH_QUESTION, quesMap);
		LogUtils.i(TAG, json);
		Response response = new Gson().fromJson(json, Response.class);
		return response;
	}

	/**
	 * 查询我的问题
	 * @param userId
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static Response queryMyQuestionList(int userId) throws ClientProtocolException, IOException {
		Map<String,String> quesMap = new HashMap<String, String>();
		quesMap.put("userId", String.valueOf(userId));
		String json = null;
		json = httpPost(Constants.PUBLISH_GET_MYQUESTION_LIST, quesMap);
		LogUtils.i(TAG, json);
		Response response = new Gson().fromJson(json, Response.class);
		return response;
	}

	/**
	 * 查询问题列表
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static Response queryQuestionList() throws ClientProtocolException, IOException {
		Map<String,String> quesMap = new HashMap<String, String>();
		String json = null;
		json = httpPost(Constants.PUBLISH_GET_QUESTION_LIST, quesMap);
		LogUtils.i(TAG, json);
		Response response = new Gson().fromJson(json, Response.class);
		return response;
	}


	public static Response deleQuestionById(int p_id) throws ClientProtocolException, IOException {
		Map<String,String> quesMap = new HashMap<String, String>();
		quesMap.put("id", p_id + "");
		String json = null;
		json = httpPost(Constants.DELE_QUESTION, quesMap);
		LogUtils.i(TAG, json);
		Response response = new Gson().fromJson(json, Response.class);
		return response;
	}

	/**
	 * 发表评论
	 * @param comment
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static Response publicComment(Comment comment) throws ClientProtocolException, IOException {
		Map<String,String> comMap = new HashMap<String, String>();
		comMap.put("userId", String.valueOf(comment.getUserId()));
		comMap.put("nickname", comment.getNickname());
		comMap.put("content", comment.getContent());
		comMap.put("questionId", String.valueOf(comment.getQuestionId()));
		
		String json = null;
		json = httpPost(Constants.PUBLISH_COMMENT, comMap);
		LogUtils.i(TAG, json);
		Response response = new Gson().fromJson(json, Response.class);
		return response;
	}

	/**
	 * 通过问题id查询评论列表
	 * @param q_id
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static Response queryCommentList(int q_id) throws ClientProtocolException, IOException {
		Map<String,String> comMap = new HashMap<String, String>();
		comMap.put("questionId", String.valueOf(q_id));
		String json = null;
		json = httpPost(Constants.QUERY_COMMENT_LIST, comMap);
		LogUtils.i(TAG, json);
		Response response = new Gson().fromJson(json, Response.class);
		return response;
	}
	/**
	 * 
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static Response queryFileList() throws ClientProtocolException, IOException {
		Map<String,String> map = new HashMap<String, String>();
		String json = null;
		json = httpPost(Constants.QUERY_FILELIST, map);
		LogUtils.i(TAG, json);
		Response response = new Gson().fromJson(json, Response.class);
		return response;
		
	}
}
