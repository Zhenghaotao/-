package com.gdufs.edu.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {
	
	
	/*
	 * 获取精准时间
	 */
	public static String getPreciseTimer() {
		DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		return sdf.format(new Date());
	}
	/*
	 * 只是获取日期
	 */
	public static String getTimer() {
		DateFormat sdf = new SimpleDateFormat("MM月dd日");
		return sdf.format(new Date());
	}
}
