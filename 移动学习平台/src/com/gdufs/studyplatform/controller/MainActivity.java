package com.gdufs.studyplatform.controller;

import java.io.File;
import java.util.List;

import android.app.TabActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.gdufs.studyplatform.R;
import com.gdufs.studyplatform.adapter.GridFileAdapter;
import com.gdufs.studyplatform.api.Api;
import com.gdufs.studyplatform.bean.FilePerate;
import com.gdufs.studyplatform.bean.ResFile;
import com.gdufs.studyplatform.bean.Response;
import com.gdufs.studyplatform.bean.User;
import com.gdufs.studyplatform.config.Constants;
import com.gdufs.studyplatform.db.DBHelper;
import com.gdufs.studyplatform.ui.FileChooseDialog;
import com.gdufs.studyplatform.ui.FileChooseDialog.CustomDialogListener;
import com.gdufs.studyplatform.util.FileUtils;
import com.gdufs.studyplatform.util.LogUtils;

public class MainActivity extends TabActivity implements OnClickListener {

	public static String TAB_MAIN_RES = "res";
	public static String TAB_MAIN_TALK = "talk";
	private static final String TAG = "MainActivity";

	// 上传文件
	List<String> fileList = null;// 文件列表，也为适配器的数据源
	FilePerate filePerate = null;// 文件操作对象
	GridFileAdapter gridAdapter = null;// GirdAdapter适配器
	private String filePath = null;// 文件路径，用于接收文件选择器返回的路径
	private File uploadFile = null;

	private Handler uploadHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Constants.UPLOAD_SUCCESS:

				Toast.makeText(MainActivity.this, "上传成功", 0).show();
				
				break;
			case Constants.UPLOAD_REPEAT:

				Toast.makeText(MainActivity.this, "同一个用户不能重复上传同一个文件", 0).show();
				break;
			case Constants.INTERNET_ERROR:

				Toast.makeText(MainActivity.this, "网络出现异常", 0).show();
				break;
			default:
				break;
			}
		};
	};

	CustomDialogListener dialogListener = new CustomDialogListener() {
		@Override
		public void getFilePath(String path) {
			filePath = path;// 得到返回的文件路径，其为一个回调方法
			// 文件名
			int index = path.lastIndexOf("/") + 1;
			String fileName = path.substring(index);
			LogUtils.i(TAG, "文件名:" + fileName);
			uploadFile = new File(path);
			ResFile resfile = new ResFile();
			User user = new DBHelper(MainActivity.this).queryUser();
			resfile.setFilename(fileName);
			resfile.setNickname(user.getNickname());
			new UploadThread(resfile).start();
		}
	};

	// 动画定义
	private Animation left_in;
	private Animation left_out;
	private Animation right_in;
	private Animation right_out;

	private Intent mResIntent;
	private Intent mTalkIntent;

	// 当前的tabId
	private int curTabId = R.id.channel1;

	public static TabHost mTabHost;

	private ImageView iv_res;
	private ImageView iv_talk;

	private TextView tv_res;
	private TextView tv_talk;

	private LinearLayout channel1;
	private LinearLayout channel2;

	//
	static final int COLOR1 = Color.parseColor("#787878");
	static final int COLOR2 = Color.parseColor("#ffffff");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initAnim();
		initIntent();
		initTab();
		initView();
	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		iv_res = (ImageView) findViewById(R.id.iv_res);
		iv_talk = (ImageView) findViewById(R.id.iv_talk);
		tv_res = (TextView) findViewById(R.id.tv_res);
		tv_talk = (TextView) findViewById(R.id.tv_talk);

		channel1 = (LinearLayout) findViewById(R.id.channel1);
		channel2 = (LinearLayout) findViewById(R.id.channel2);
		channel1.setOnClickListener(this);
		channel2.setOnClickListener(this);

		iv_res.setImageResource(R.drawable.res_selected);
		tv_res.setTextColor(COLOR2);
	}

	private void initTab() {
		mTabHost = getTabHost();
		mTabHost.addTab(buildTabSpec(TAB_MAIN_RES, R.string.main_res,
				R.drawable.res_normal, mResIntent));
		mTabHost.addTab(buildTabSpec(TAB_MAIN_TALK, R.string.main_talk,
				R.drawable.talk_normal, mTalkIntent));
	}

	private TabHost.TabSpec buildTabSpec(String tag, int resLabel, int resIcon,
			final Intent content) {
		return mTabHost
				.newTabSpec(tag)
				.setIndicator(getString(resLabel),
						getResources().getDrawable(resIcon))
				.setContent(content);
	}

	/**
	 * 初始化意图
	 */
	private void initIntent() {
		mResIntent = new Intent(this, ResActivity.class);
		mTalkIntent = new Intent(this, TalkActivity.class);
	}

	/**
	 * 初始化动画
	 */
	private void initAnim() {
		left_in = AnimationUtils.loadAnimation(this, R.anim.left_in);
		left_out = AnimationUtils.loadAnimation(this, R.anim.left_out);
		right_in = AnimationUtils.loadAnimation(this, R.anim.right_in);
		right_out = AnimationUtils.loadAnimation(this, R.anim.right_out);
	}

	private long exitTime = 0;

	public void backTo() {
		if ((System.currentTimeMillis() - exitTime) > 2000) {
			Toast.makeText(getApplicationContext(), "亲，再按一次才能退出哦！",
					Toast.LENGTH_SHORT).show();
			exitTime = System.currentTimeMillis();
		} else {
			finish();
			System.exit(0);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_menu, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		if (id == R.id.menu_setting) {
			Intent intent = new Intent();
			intent.setClass(this, AboutActivity.class);
			startActivity(intent);
			return true;
		} else if (id == R.id.menu_edit) {
			LogUtils.i(TAG, "edit  !!!! edit");
			Intent intent = new Intent();
			intent.setClass(this, PublishQuestionActivity.class);
			startActivity(intent);
			return true;
		} else if (id == R.id.menu_upload) {
			LogUtils.i(TAG, "文件选择上传");
			selectFile();
		} else if (id == R.id.menu_downloaded) {
			selectFileToOpen();
		}
		return super.onOptionsItemSelected(item);
	}

	public void selectFileToOpen() {
		// 如果没有SD卡，则输出提示
		if (FilePerate.getRootFolder() == null) {
			Toast.makeText(this, "没有SD卡", Toast.LENGTH_SHORT).show();
			return;
		}
		// 创建一个自定义的对话框
		FileChooseDialog dialog = new FileChooseDialog(this,
				new CustomDialogListener() {

					@Override
					public void getFilePath(String path) {
						File file = new File(path);
						FileUtils.openFile(file, MainActivity.this);
					}
				}, Constants.BASE_PATH);
		dialog.setTitle("选择指定文件");
		dialog.show();// 显示对话框
	}

	// 选择文件
	public void selectFile() {
		// 如果没有SD卡，则输出提示
		if (FilePerate.getRootFolder() == null) {
			Toast.makeText(this, "没有SD卡", Toast.LENGTH_SHORT).show();
			return;
		}
		// 创建一个自定义的对话框
		FileChooseDialog dialog = new FileChooseDialog(this, dialogListener,
				null);
		dialog.setTitle("请要选择文件上传");
		dialog.show();// 显示对话框
	}

	/**
	 * 回退键,按两次才推出
	 */
	@Override
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			backTo();
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_MENU) {

		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v) {
		if (curTabId == v.getId()) {
			return;
		}
		iv_res.setImageResource(R.drawable.res_normal);
		iv_talk.setImageResource(R.drawable.talk_normal);

		tv_res.setTextColor(COLOR1);
		tv_talk.setTextColor(COLOR1);

		int checkedId = v.getId();

		final boolean o;
		if (curTabId < checkedId)
			o = true;
		else
			o = false;
		if (o)
			mTabHost.getCurrentView().startAnimation(left_out);
		else
			mTabHost.getCurrentView().startAnimation(right_out);
		switch (checkedId) {
		case R.id.channel1:
			mTabHost.setCurrentTabByTag(TAB_MAIN_RES);
			iv_res.setImageResource(R.drawable.res_selected);
			tv_res.setTextColor(COLOR2);
			break;

		case R.id.channel2:
			mTabHost.setCurrentTabByTag(TAB_MAIN_TALK);
			iv_talk.setImageResource(R.drawable.talk_selected);
			tv_talk.setTextColor(COLOR2);
			break;

		default:
			break;
		}
		if (o)
			mTabHost.getCurrentView().startAnimation(left_in);
		else
			mTabHost.getCurrentView().startAnimation(right_in);
		curTabId = checkedId;
	}

	private class UploadThread extends Thread {
		private ResFile resfile;

		public UploadThread(ResFile resfile) {
			this.resfile = resfile;
		}

		@Override
		public void run() {
			Message msg = uploadHandler.obtainMessage();
			try {
				Response res = Api.uploadFile(resfile, uploadFile);
				msg.what = res.getEchoCode();
			} catch (Exception e) {
				LogUtils.e(TAG, e.getMessage());
				msg.what = Constants.INTERNET_ERROR;
			}
			uploadHandler.sendMessage(msg);
		}
	}

}
