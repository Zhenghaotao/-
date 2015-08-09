package com.gdufs.studyplatform.util;

import android.util.Log;

import com.gdufs.studyplatform.config.Constants;

public class LogUtils {
	
	
	private static boolean isLog = Constants.DEVELOPER_MODE;
	
	/**
	 * 错误
	 * @param tag
	 * @param msg
	 */
	public static void e(String tag,String msg){
		if(isLog){
			Log.e(tag, msg + "");
		}
	}
	
	/**
	 * 信息
	 * @param tag
	 * @param msg
	 */
	public static void i(String tag,String msg){
		if(isLog){
			Log.i(tag, msg + "");
		}
	}
	/**
	 * 警告
	 * @param tag
	 * @param msg
	 */
	public static void w(String tag,String msg){
		if(isLog){
			Log.i(tag, msg + "");
		}
	}
	
	
	
}
