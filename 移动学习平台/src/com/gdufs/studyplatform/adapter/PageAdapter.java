package com.gdufs.studyplatform.adapter;

import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public class PageAdapter extends PagerAdapter {
	
	private List<View> views;
	
	public PageAdapter(List<View> views) {
		super();
		this.views = views;
	}
	@Override
	public int getCount() {
		return views.size();
	}
	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}
	@Override
	public void destroyItem(ViewGroup view, int position, Object arg2) {
		view.removeView(views.get(position));
	}
	@Override
	public Object instantiateItem(ViewGroup view, int position) {
		try {
			if (views.get(position).getParent() == null) {
				view.addView(views.get(position));
			} else {
				((ViewGroup) views.get(position).getParent())
						.removeView(views.get(position));
				view.addView(views.get(position));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return views.get(position);
	}
}
