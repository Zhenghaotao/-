package com.gdufs.studyplatform.frame.res;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.gdufs.studyplatform.R;
import com.gdufs.studyplatform.adapter.MyUploadFileListViewAdapter;
import com.gdufs.studyplatform.base.BaseFragment;
import com.gdufs.studyplatform.bean.ResFile;
import com.gdufs.studyplatform.bean.User;
import com.gdufs.studyplatform.db.DBHelper;
import com.gdufs.studyplatform.ui.CustomListView;
import com.gdufs.studyplatform.ui.CustomListView.OnRefreshListener;
import com.gdufs.studyplatform.ui.CustomListView.OnScrollPositionListener;

public class RecordFrag extends BaseFragment  implements OnClickListener,OnScrollPositionListener, OnRefreshListener{
	private View view;
	private Context context;
	private List<ResFile> data  = new ArrayList<ResFile>();
	
	private MyUploadFileListViewAdapter adapter;
	
	private CustomListView lv_show;
	private ImageView btn_backTop;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		context = getActivity();
		view = inflater.inflate(R.layout.frame_record, container,false);
		adapter = new MyUploadFileListViewAdapter(context, data);
		initView();
		initData();
		lv_show.setAdapter(adapter);
		return view;
	}

	@Override
	public void initView() {
		lv_show = (CustomListView) view.findViewById(R.id.lv_show);
		btn_backTop = (ImageView) view.findViewById(R.id.btn_backTop);
		
		btn_backTop.setOnClickListener(this);
		lv_show.setOnScrollPositionListener(this);
		lv_show.setOnRefreshListener(this);
		
	}

	@Override
	public void initData() {
		onRefresh();
	}
	
	@Override
	public void dissCommentPanel() {
	}

	@Override
	public void showEditCommentView() {
	}

	@Override
	public void onRefresh() {
		DBHelper helper = new DBHelper(context);
		User user  = helper.queryUser();
		List<ResFile> newData = helper.queryResFileList(user.getNickname());
		adapter.refreshData(newData);
		helper.close();
		lv_show.refreshComplete();
		lv_show.showMoreComplete();
	}

	@Override
	public void onShowNextPage() {
	}


	@Override
	public void showBackTopButton() {
		btn_backTop.setVisibility(View.VISIBLE);
	}
	@Override
	public void hideBackTopButton() {
		btn_backTop.setVisibility(View.GONE);
	}
	@Override
	public void backTopButtonFocuse() {
		btn_backTop.setPressed(true);
	}
	@Override
	public void backTopButtonBlur() {
		btn_backTop.setPressed(false);
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_backTop:
			lv_show.setSelection(0);
			break;

		default:
			break;
		}
	}
	

}
