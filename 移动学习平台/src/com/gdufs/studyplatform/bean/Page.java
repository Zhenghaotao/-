package com.gdufs.studyplatform.bean;

import java.util.List;

import com.gdufs.studyplatform.config.Constants;

public class Page {
	private int count;
	public int size =Constants.PAGE_SIZE;
	
	private int currentPage = 1;
	private int countPage;
	private String order;
	private List<?> dataList;
	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getCurrentPage() {
	 return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	
	public int getCountPage() {
		
		countPage = (count-1)/size+1;
		return countPage;
	}
	
	public boolean hasNextPage(){
		return currentPage<getCountPage();
	}
	public boolean isFristPage(){
		return currentPage==1;
	}
	public boolean isLastPage(){
		return currentPage==getCountPage();
	}

	public List<?> getDataList() {
		return dataList;
	}

	public void setDataList(List<?> dataList) {
		this.dataList = dataList;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}
	
}
