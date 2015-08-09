package com.gdufs.edu.util;

import java.util.Collection;

public class CommonUtil {
	
	
	
	/**
	 * 判断集合的有效性 
	 */
	public static boolean isValid(Collection col){
		if(col == null || col.isEmpty()){
			return false ;
		}
		return true ;
	}
}
