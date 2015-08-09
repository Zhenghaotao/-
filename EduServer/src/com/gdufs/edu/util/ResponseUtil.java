package com.gdufs.edu.util;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import com.gdufs.edu.model.Response;
import com.google.gson.Gson;

public class ResponseUtil {
	public static void responseToUser( HttpServletResponse response,Response res){
		response.setContentType("text/json;charset=UTF-8");
		try {
			response.getWriter().print(new Gson().toJson(res));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
