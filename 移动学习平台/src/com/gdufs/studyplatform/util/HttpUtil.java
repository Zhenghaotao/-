package com.gdufs.studyplatform.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpUtil {

	public static InputStream getResource(String path) throws IOException {
		URL url = new URL(path);
		HttpURLConnection conn  = (HttpURLConnection) url.openConnection();
		InputStream is = conn.getInputStream();
		
		
		return is;
	}
	
	
	
}
