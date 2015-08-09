package com.gdufs.studyplatform.frame.talk;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.gdufs.studyplatform.R;
import com.gdufs.studyplatform.adapter.QuestionListViewAdapter;
import com.gdufs.studyplatform.api.Api;
import com.gdufs.studyplatform.base.BaseFragment;
import com.gdufs.studyplatform.bean.Comment;
import com.gdufs.studyplatform.bean.Question;
import com.gdufs.studyplatform.bean.Response;
import com.gdufs.studyplatform.bean.User;
import com.gdufs.studyplatform.config.Constants;
import com.gdufs.studyplatform.controller.QuestionDetailActivity;
import com.gdufs.studyplatform.db.DBHelper;
import com.gdufs.studyplatform.ui.CustomListView;
import com.gdufs.studyplatform.ui.CustomListView.OnRefreshListener;
import com.gdufs.studyplatform.ui.CustomListView.OnScrollPositionListener;
import com.gdufs.studyplatform.util.Common;
import com.gdufs.studyplatform.util.LogUtils;

public class MyQuestionFrag extends BaseFragment implements OnClickListener,
		OnScrollPositionListener, OnItemClickListener, OnRefreshListener {

	private static final String TAG = "MyQuestionFrag";

	private View view;
	private Context context;
	// 自己的id号
	private int userId;
	private List<Question> data = new ArrayList<Question>();
	private QuestionListViewAdapter adapter;
	private CustomListView lv_show;
	private ImageView btn_backTop;
	private Button btn_reload;
	private Button btn_publish_comment;
	private Button btn_close;
	
	private EditText et_comement_content;
	
	
	// 当前页数(暂时没用)
	private int curPage = 1;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case Constants.MY_QUESTION:
				Response res = (Response) msg.obj;
				List<Question> infos = res.getQuestionList();
//				saveData(infos);
				adapter.refreshData(infos);
				adapter.notifyDataSetChanged();
				
				break;
			case Constants.INTERNET_ERROR:

				Toast.makeText(context, "网络出现异常", 0).show();
				break;
			default:
				break;
			}
			lv_show.refreshComplete();
			lv_show.showMoreComplete();
		};
	};
	private Handler delHandler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Constants.DELE_QUESTION_SUCCESS:
				int q_id = msg.arg1;
				int index = msg.arg2;
				new DBHelper(context).deleQuestionById(q_id);
				data.remove(index);
				adapter.refreshData(data);
				adapter.notifyDataSetChanged();
				onRefresh();
				dissCommentPanel();
				break;
			case Constants.INTERNET_ERROR:
				Toast.makeText(context, "网络出现异常", 0).show();
			default:
				break;
			}
		};
	};

	private Comment comment;
	
	private Handler commentHandler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Constants.PUBLISH_COMMENT_SUCCESS:
				onRefresh();
				dissCommentPanel();
				break;
			case Constants.INTERNET_ERROR:
				Toast.makeText(context, "网络出现异常", 0).show();
				break;
			default:
				break;
			}
		};
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		context = getActivity();
		view = inflater.inflate(R.layout.frame_myquestion, container, false);
		initView();
		initData();
		adapter = new QuestionListViewAdapter(this,context, data, lv_show);
		lv_show.setAdapter(adapter);
		lv_show.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				final int index = position - 1;
				Question question = data.get(index);
				final int q_id = question.getId();
				
				LogUtils.i(TAG, position + "");
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setTitle("提示")
						.setCancelable(false)
						.setMessage("确定要删除吗?")
						.setPositiveButton("删除",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										new DelQuestionThread(q_id,index).start();
										
									}
								})
						.setNegativeButton("取消",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();// 销毁对话框
									}
								});
				builder.show();
				return true;
			}
		});
		return view;
	}

	public void initData() {
		DBHelper helper = new DBHelper(context);
		User user = helper.queryUser();
		userId = user.getId();
		// 判断有没有联网
		List<Question> temp = helper.queryQuestionList(userId);
		data.addAll(temp);
	}

	public void initView() {
		lv_show = (CustomListView) view.findViewById(R.id.lv_show);
		btn_backTop = (ImageView) view.findViewById(R.id.btn_backTop);
		btn_reload = (Button) view.findViewById(R.id.btn_reload);
		btn_publish_comment = (Button) view.findViewById(R.id.btn_publish_comment);
		btn_close = (Button) view.findViewById(R.id.btn_close);
		et_comement_content = (EditText) view.findViewById(R.id.et_comement_content);

		lv_show.setOnScrollPositionListener(this);
		lv_show.setOnItemClickListener(this);
		lv_show.setOnRefreshListener(this);

		btn_backTop.setOnClickListener(this);
		btn_reload.setOnClickListener(this);
		btn_publish_comment.setOnClickListener(this);
		btn_close.setOnClickListener(this);
	}

	protected void saveData(final List<Question> data) {
		new Thread() {
			public void run() {
//				new DBHelper(context).saveQuestionList(data, userId);
			};
		}.start();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		int index = position - 1;
		Question ques = data.get(index);
		LogUtils.i(TAG, "点击了第 " + position + " 条");
		Intent intent  = new Intent(context,QuestionDetailActivity.class);
		intent.putExtra("question", ques);
		startActivity(intent);
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

		case R.id.btn_reload:
			view.findViewById(R.id.progressView).setVisibility(View.VISIBLE);
			view.findViewById(R.id.iv_busy).setVisibility(View.GONE);
			
		case R.id.btn_publish_comment:
			String content = et_comement_content.getText().toString();
			int q_id = adapter.getQ_id();
			if(TextUtils.isEmpty(content)){
				Toast.makeText(context, "回复内容不能为空", 0).show();
				return ;
			}
			User user = new DBHelper(context).queryUser();
			comment = new Comment();
			comment.setUserId(user.getId());
			comment.setNickname(user.getNickname());
			comment.setQuestionId(q_id);
			comment.setContent(content);
			new Thread(){
				public void run() {
					Message msg = commentHandler.obtainMessage();
					try {
						Response res = Api.publicComment(comment);
						msg.what = res.getEchoCode();
						
					} catch (Exception e) {
						LogUtils.e(TAG, e.getMessage());
						msg.what = Constants.INTERNET_ERROR;
					}
					commentHandler.sendMessage(msg);
				};
				
			}.start();
			
			break;
		case R.id.btn_close:
			dissCommentPanel();
		default:
			break;
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		onRefresh();
	}

	@Override
	public void onRefresh() {
		new QueryMyQuestionListThread().start();
	}

	@Override
	public void onShowNextPage() {

	}
	
	private class DelQuestionThread extends Thread{
		private int q_id;
		private int index;
		public DelQuestionThread(int q_id,int index) {
			this.q_id = q_id;
			this.index = index;
		}
		
		@Override
		public void run() {
			Message msg = delHandler.obtainMessage();
			try {
				LogUtils.i(TAG, "开始从服务器那里删除数据");
				Response res = Api.deleQuestionById(q_id);
				msg.what = res.getEchoCode();
				msg.obj = q_id;
			} catch (Exception e) {
				LogUtils.e(TAG, e.getMessage());
				msg.what = Constants.INTERNET_ERROR;
			}
			delHandler.sendMessage(msg);
		}
	}

	private class QueryMyQuestionListThread extends Thread {
		@Override
		public void run() {
			Message msg = mHandler.obtainMessage();
			try {
				LogUtils.i(TAG, "开始向服务器拿自己的问题列表数据....");
				Response res = Api.queryMyQuestionList(userId);
				msg.what = res.getEchoCode();
				msg.obj = res;
			} catch (Exception e) {
				LogUtils.e(TAG, e.getMessage());
				msg.what = Constants.INTERNET_ERROR;
			}
			mHandler.sendMessage(msg);
		}
	}
	public void showEditCommentView(){
		et_comement_content.setText("");
		view.findViewById(R.id.publicCommentView).setFocusable(true);
		view.findViewById(R.id.publicCommentView).setVisibility(View.VISIBLE);
		view.findViewById(R.id.publicCommentView).startAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_in_from_bottom));
		adapter.setShow(true);
	}
	
	public void dissCommentPanel(){
		
		lv_show.setFocusable(true);
		Animation anim = AnimationUtils.loadAnimation(context, R.anim.slide_out_to_bottom);
		anim.setAnimationListener(new AnimationListener(){
			@Override
			public void onAnimationEnd(Animation arg0) {
				view.findViewById(R.id.publicCommentView).setVisibility(View.GONE);
			}

			@Override
			public void onAnimationRepeat(Animation arg0) {}
			@Override
			public void onAnimationStart(Animation arg0) {}});
		view.findViewById(R.id.publicCommentView).startAnimation(anim);
		adapter.setShow(false);
	}
}
