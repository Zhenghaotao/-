package com.gdufs.studyplatform.frame.res;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;

import com.gdufs.studyplatform.R;
import com.gdufs.studyplatform.adapter.ResFileListViewAdapter;
import com.gdufs.studyplatform.api.Api;
import com.gdufs.studyplatform.base.BaseFragment;
import com.gdufs.studyplatform.bean.ResFile;
import com.gdufs.studyplatform.bean.Response;
import com.gdufs.studyplatform.config.Constants;
import com.gdufs.studyplatform.db.DBHelper;
import com.gdufs.studyplatform.ui.CustomListView;
import com.gdufs.studyplatform.ui.CustomListView.OnRefreshListener;
import com.gdufs.studyplatform.ui.CustomListView.OnScrollPositionListener;
import com.gdufs.studyplatform.util.Common;
import com.gdufs.studyplatform.util.LogUtils;

public class ShareFrag extends BaseFragment implements OnClickListener,
		OnScrollPositionListener, OnRefreshListener {
	private static final String TAG = "ShareFrag";
	private View view;
	private Context context;

	private ResFileListViewAdapter adapter;
	private ImageView btn_backTop;
	private CustomListView lv_show;
	private List<ResFile> data = new ArrayList<ResFile>();

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {

			case Constants.QUERY_UPLOADFILE_LIST:
				Response res = (Response) msg.obj;
				List<ResFile> newData = res.getResfileList();
				adapter.refreshData(newData);
				saveData(newData);
				break;
			case Constants.INTERNET_ERROR:
				Toast.makeText(context, "网络出现异常", 0).show();
				adapter.notifyDataSetChanged();
				break;

			default:
				break;
			}
			lv_show.refreshComplete();
			lv_show.showMoreComplete();
		};
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		context = getActivity();
		view = inflater.inflate(R.layout.frame_share, container, false);
		initView();
		initData();
		adapter = new ResFileListViewAdapter(context, data);
		lv_show.setAdapter(adapter);
		return view;
	}

	protected void saveData(List<ResFile> newData) {
		new DBHelper(context).saveResfileList(newData);
	}

	@Override
	public void initView() {
		lv_show = (CustomListView) view.findViewById(R.id.lv_show);
		btn_backTop = (ImageView) view.findViewById(R.id.btn_backTop);
		lv_show.setOnScrollPositionListener(this);
		lv_show.setOnRefreshListener(this);
		btn_backTop.setOnClickListener(this);
	}

	@Override
	public void initData() {
		List<ResFile> temp = new DBHelper(context).queryResFileList(null);
		data.addAll(temp);
		if (Common.isNetworkConnected(context)) {
			new QueryUploadFileThread().start();
		}
	}

	@Override
	public void dissCommentPanel() {
	}

	@Override
	public void showEditCommentView() {
	}

	@Override
	public void onRefresh() {
		if (!Common.isNetworkConnected(context)) {
			Toast.makeText(context, "请先连好网络", 0).show();
			return;
		}
		new QueryUploadFileThread().start();
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

	private class QueryUploadFileThread extends Thread {
		@Override
		public void run() {
			Message msg = mHandler.obtainMessage();
			try {
				Response res = Api.queryFileList();
				msg.what = res.getEchoCode();
				msg.obj = res;
			} catch (Exception e) {
				LogUtils.e(TAG, e.getMessage());
				msg.what = Constants.INTERNET_ERROR;
			}
			mHandler.sendMessage(msg);
		}
	}
}
