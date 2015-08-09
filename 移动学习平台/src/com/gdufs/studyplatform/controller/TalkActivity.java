package com.gdufs.studyplatform.controller;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabContentFactory;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.gdufs.studyplatform.R;
import com.gdufs.studyplatform.adapter.PageAdapter;
import com.gdufs.studyplatform.util.DethPageTransformer;

public class TalkActivity extends FragmentActivity{
	
	private String[] pageIds = {
			"question_menu","myquesion"
	};
	private int[] textIds = {
		R.string.frag_questionmenu,R.string.frag_myquestion	
	};
	
	private TabHost tabHost;
	private List<View> views;
	private FragmentManager manager;
	private ViewPager pager;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_talk);
		initView();
		initTab();
		initViewPager();
	}


	private void initViewPager() {
		pager.setAdapter(new PageAdapter(views));
		pager.setPageTransformer(true, new DethPageTransformer());
		pager.setOnPageChangeListener(new PageChangeListener());
		tabHost.setOnTabChangedListener(new TabChangeListener());
	}


	private void initTab() {
		// 管理tabHost开始
		tabHost.setup();
		TabContentFactory factory = new TabContentFactory(){

			@Override
			public View createTabContent(String tag) {
				return new View(TalkActivity.this);
			}
		};
		
		for (int i = 0; i < pageIds.length; i++) {
			tabHost.addTab(createTabSepc(pageIds[i], textIds[i], factory));
		}
		tabHost.setCurrentTab(0);
		// 管理tabHost结束
	}
	
	private TabSpec createTabSepc(String pageId, int textId,
			TabContentFactory factory) {
		TabSpec tabSpec = tabHost.newTabSpec(pageId);
		tabSpec.setIndicator(createTabView(textId));
		tabSpec.setContent(factory);
		return tabSpec;
	}
	
	/**
	 * 创建tab View
	 * 
	 * @param string
	 * @return
	 */
	private View createTabView(int textId) {
		View tabView = getLayoutInflater().inflate(R.layout.tab, null);
		TextView textView = (TextView) tabView.findViewById(R.id.tab_text);
		textView.setText(textId);
		return tabView;
	}


	private void initView() {
		pager = (ViewPager) findViewById(R.id.talk_viewpager);
		tabHost = (TabHost) findViewById(R.id.talk_host);
		manager = getSupportFragmentManager();
		views = new ArrayList<View>();
		
		views.add(manager.findFragmentById(R.id.frag_question_menu).getView());
		views.add(manager.findFragmentById(R.id.frag_myquestion).getView());
	}
	
	/**
	 * 标签页点击切换监听器
	 * 
	 * @author Administrator
	 * 
	 */
	private class TabChangeListener implements OnTabChangeListener {
		@Override
		public void onTabChanged(String tabId) {
			for (int i = 0; i < pageIds.length; i++) {
				if (pageIds[i].equals(tabId)) {
					pager.setCurrentItem(i);
					return;
				}
			}

		}
	}

	/**
	 * ViewPager滑动切换监听器
	 * 
	 * @author Administrator
	 * 
	 */
	private class PageChangeListener implements OnPageChangeListener {
		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageSelected(int arg0) {
			tabHost.setCurrentTab(arg0);
		}
	}
}
