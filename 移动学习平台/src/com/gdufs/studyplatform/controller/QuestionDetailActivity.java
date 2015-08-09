package com.gdufs.studyplatform.controller;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.gdufs.studyplatform.R;
import com.gdufs.studyplatform.adapter.CommentListViewAdapter;
import com.gdufs.studyplatform.api.Api;
import com.gdufs.studyplatform.bean.Comment;
import com.gdufs.studyplatform.bean.Question;
import com.gdufs.studyplatform.bean.Response;
import com.gdufs.studyplatform.config.Constants;
import com.gdufs.studyplatform.db.DBHelper;
import com.gdufs.studyplatform.ui.CustomImageView;
import com.gdufs.studyplatform.ui.CustomImageView.OnMeasureListener;
import com.gdufs.studyplatform.ui.CustomListView.OnRefreshListener;
import com.gdufs.studyplatform.ui.CustomListView.OnScrollPositionListener;
import com.gdufs.studyplatform.ui.CustomListView;
import com.gdufs.studyplatform.util.Common;
import com.gdufs.studyplatform.util.LogUtils;
import com.gdufs.studyplatform.util.NativeImageLoader;
import com.gdufs.studyplatform.util.NativeImageLoader.NativeImageCallBack;

public class QuestionDetailActivity extends Activity implements OnScrollPositionListener,OnRefreshListener{
	private static final String TAG = "QuestionDetailActivity";
	
	private Point mPoint = new Point(0, 0);

	private Question question;
	private List<Comment> comments = new ArrayList<Comment>();
	
	private ImageView iv_backTop;
	
	private CustomListView lv_comment;
	
	private CommentListViewAdapter adapter;
	
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case Constants.GAIN_COMMENT_SUCCESS:
				Response res = (Response) msg.obj;
				List<Comment> newData = res.getCommentList();
				adapter.refreshData(newData);
				saveData(comments);
				break;
			case Constants.INTERNET_ERROR:
				Toast.makeText(QuestionDetailActivity.this, "网络出现异常", 0).show();
				adapter.notifyDataSetChanged();
				break;
			default:
				break;
			}
			lv_comment.refreshComplete();
		};
	};
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_question_detail);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		question = (Question) getIntent().getSerializableExtra("question");
		initView();
		initData();
		adapter = new CommentListViewAdapter(this, comments, question.getUserId());
		lv_comment.setAdapter(adapter);
	}


	protected void saveData(List<Comment> data) {
		new DBHelper(this).saveComment(data,question.getId());
	}


	private void initData() {
		int q_id = question.getId();
		List<Comment> temp = new DBHelper(this).queryCommentByQid(q_id);
		if(temp != null){
			comments.addAll(temp);
		}
		if(Common.isNetworkConnected(this)){
			new QueryCommentsThread(q_id).start();
		}
	}


	private void initView() {
		
		iv_backTop = (ImageView) findViewById(R.id.iv_backTop);
		
		lv_comment = (CustomListView) findViewById(R.id.lv_comment);
		lv_comment.setOnRefreshListener(this);
		lv_comment.setOnScrollPositionListener(this);
		
		iv_backTop.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				lv_comment.setSelection(0);
			}
		});
	}
	
	private class QueryCommentsThread extends Thread{
		private int q_id;
		public QueryCommentsThread(int q_id) {
			this.q_id = q_id;
		}
		
		@Override
		public void run() {
			Message  msg = mHandler.obtainMessage();
			try {
				Response res = Api.queryCommentList(q_id);
				msg.what = res.getEchoCode();
				msg.obj = res;
			} catch (Exception e) {
				LogUtils.e(TAG, e.getMessage());
				msg.what = Constants.INTERNET_ERROR;
			}
			mHandler.sendMessage(msg);
		}
		
	}

	@Override
	public void onRefresh() {
		new QueryCommentsThread(question.getId()).start();
	}


	@Override
	public void onShowNextPage() {
		
	}


	@Override
	public void showBackTopButton() {
		iv_backTop.setVisibility(View.VISIBLE);
		
	}


	@Override
	public void hideBackTopButton() {
		iv_backTop.setVisibility(View.GONE);
	}


	@Override
	public void backTopButtonFocuse() {
		iv_backTop.setPressed(true);
	}


	@Override
	public void backTopButtonBlur() {
		iv_backTop.setPressed(false);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
}
