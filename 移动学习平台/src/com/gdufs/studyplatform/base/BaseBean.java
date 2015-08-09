package com.gdufs.studyplatform.base;

import java.io.Serializable;

public class BaseBean implements Serializable{

	private static final long serialVersionUID = 1L;

	protected int id;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
