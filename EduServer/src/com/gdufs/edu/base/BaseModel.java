package com.gdufs.edu.base;

import java.io.Serializable;
/**
 * 
 * @author zhenghaotao
 * BaseModel.java
 * 2015年5月10日
 */
public class BaseModel implements Serializable {

	private static final long serialVersionUID = -8916021861224775448L;
	protected int id;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
}
