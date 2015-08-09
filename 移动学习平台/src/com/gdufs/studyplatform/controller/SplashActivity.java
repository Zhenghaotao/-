package com.gdufs.studyplatform.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.gdufs.studyplatform.R;
import com.gdufs.studyplatform.api.Api;
import com.gdufs.studyplatform.bean.Response;
import com.gdufs.studyplatform.bean.User;
import com.gdufs.studyplatform.config.Constants;
import com.gdufs.studyplatform.db.DBHelper;
import com.gdufs.studyplatform.ui.LoadingView;
import com.gdufs.studyplatform.util.Common;
import com.gdufs.studyplatform.util.LogUtils;
import com.gdufs.studyplatform.util.StreamUtil;

public class SplashActivity extends Activity {

	private static final String TAG = "SplashActivity";
	private String version;
	private String description;
	private String apkurl;
	private String appName;

	private int delayTime = Constants.DELAYTIME;
	private long startTime;// 开始时间

	//
	protected static final int ENTER_HOME = 0;
	protected static final int SHOW_UPDATE_DIALOG = 1;
	protected static final int URL_ERROR = 2;
	protected static final int NETWORK_ERROR = 3;
	protected static final int JSON_ERROR = 4;
	
	

	//
	private TextView tv_splash_version;
	private TextView tv_update_info;
	private LoadingView loadingView;

	// 暂时不用
	private SharedPreferences sp;
	
	
	private DBHelper helper;
	/**
	 * 处理子线程中各种返回的结果
	 */
	private Handler updateHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SHOW_UPDATE_DIALOG: // 显示升级的对话框
				LogUtils.i(TAG, "显示升级的对话框");
				showUpdateDialog();
				break;
			case ENTER_HOME: // 进入主页面
				checkUserInfo();
				break;
			case URL_ERROR: // URL错误
				checkUserInfo();
				Toast.makeText(getApplicationContext(), "URL错误", 0).show();
				break;
			case NETWORK_ERROR: // 网络异常
				Toast.makeText(getApplicationContext(), "网络错误", 0).show();
				checkUserInfo();
				break;
			case JSON_ERROR: // JSON解析出错
				Toast.makeText(SplashActivity.this, "JSON错误", 0).show();
				checkUserInfo();
				break;
			}
		};
	};
	
	private Handler checkHandler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Constants.REGISTERED:
				Response res = (Response) msg.obj;
				User user = res.getUser();
				helper.saveUser(user);
				helper.close();
				enterHome();
				break;
			case Constants.NO_REGISTERED:
				helper.close();
				register();
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		sp = getSharedPreferences("config", Context.MODE_PRIVATE);
		startTime = System.currentTimeMillis();
		// 检查数据库是否创建
		LogUtils.i(TAG, "检查数据库是否创建");
		helper = new DBHelper(this);
		helper.createTable(helper.getWritableDatabase());
		
		boolean update = sp.getBoolean("update", false);
		initView();
		installShortCut();
		startLoaing();
		// 检测app目录时候已经有了
		File file = new File(Constants.IMAGE_DIR);
		if (!file.exists()) {
			file.mkdirs();
		}

		if (update) {
			Log.i(TAG, "start Checking");
			// 检查升级
			checkUpdate();
		} else {
			// 自动升级已经关闭
			updateHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					checkUserInfo();
				}
			}, 1000);
		}

	}

	private void startLoaing() {
		new Thread() {
			public void run() {
				loadingView.startAnim();
			};
		}.start();
	}

	private void initView() {
		tv_splash_version = (TextView) findViewById(R.id.tv_splash_version);
		tv_splash_version.setText("版本号: " + getVersionName());

		tv_update_info = (TextView) findViewById(R.id.tv_update_info);

		loadingView = (LoadingView) findViewById(R.id.lv_loadingView);
		loadingView.setVisibility(View.VISIBLE);
		initLoadingImages();
	}

	// 得到应用程序的版本名称
	private String getVersionName() {
		// 用来管理手机的APK
		PackageManager pm = getPackageManager();
		// 得到指定APK的功能清单文件
		try {
			PackageInfo info = pm.getPackageInfo(getPackageName(), 0);
			return info.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return "";
		}
	}

	private void initLoadingImages() {
		int[] imageIds = new int[6];
		imageIds[0] = R.drawable.loader_frame_1;
		imageIds[1] = R.drawable.loader_frame_2;
		imageIds[2] = R.drawable.loader_frame_3;
		imageIds[3] = R.drawable.loader_frame_4;
		imageIds[4] = R.drawable.loader_frame_5;
		imageIds[5] = R.drawable.loader_frame_6;

		loadingView.setImageIds(imageIds);
	}

	/**
	 * 创建快捷图标
	 */
	public void installShortCut() {
		boolean shortcut = sp.getBoolean("shortcut", false);
		if (shortcut)
			return;
		Editor editor = sp.edit();
		// 发送广播的意图，要创建快捷图标了
		Intent intent = new Intent();
		intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
		// 快捷方式 要包含3个重要的信息 1，名称 2.图标 3.干什么事情
		intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "移动学习平台");
		intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, BitmapFactory
				.decodeResource(getResources(), R.drawable.ic_launcher));
		// 桌面点击图标对应的意图。
		Intent shortcutIntent = new Intent();
		shortcutIntent.setAction("android.intent.action.MAIN");
		shortcutIntent.addCategory("android.intent.category.LAUNCHER");
		shortcutIntent.setClassName(getPackageName(),
				"com.gdufs.studyplatform.controller.SplashActivity");
		intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
		sendBroadcast(intent);
		editor.putBoolean("shortcut", true);
		editor.commit();
	}

	private void enterHome() {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		// 关闭当前Activity页面
		finish();
	}

	// 弹出升级对话框
	private void showUpdateDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("提示升级");
		builder.setCancelable(false);// 除了对话框的选项,其他都不能
		// 监听取消的事件
		builder.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				// 进入主页面
				checkUserInfo();
				dialog.dismiss();
			}
		});
		builder.setMessage(description);
		builder.setPositiveButton("立刻升级", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// 下载APK，并且替换安装
				if (Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					// 如果sdcard存在
					// afnal
					FinalHttp finalhttp = new FinalHttp();
					finalhttp.download(apkurl, Environment
							.getExternalStorageDirectory().getAbsolutePath()
							+ File.separator + appName,
							new AjaxCallBack<File>() {
								@Override
								public void onFailure(Throwable t, int errorNo,
										String strMsg) {
									t.printStackTrace();
									Toast.makeText(getApplicationContext(),
											"下载失败", 1).show();
									super.onFailure(t, errorNo, strMsg);
								}
								
								@Override
								public void onLoading(long count, long current) {
									super.onLoading(count, current);
									// 当前下载百分比
									tv_update_info.setVisibility(View.VISIBLE);
									int progress = (int) (current * 100 / count);
									tv_update_info.setText("下载进度: " + progress
											+ "%");
								}

								@Override
								public void onSuccess(File t) {
									super.onSuccess(t);
									installAPK(t);
								}

								// 安装APK
								private void installAPK(File t) {
									Intent intent = new Intent();
									intent.setAction("android.intent.action.VIEW");
									intent.addCategory("android.intent.category.DEFAULT");
									intent.setDataAndType(Uri.fromFile(t),
											"application/vnd.android.package-archive");
									startActivity(intent);
								}

							});
				} else {
					Toast.makeText(getApplicationContext(), "没有sdcard,请安装上在试",
							0).show();
				}
			}
		});
		builder.setNegativeButton("下次再说", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();// 销毁对话框
				checkUserInfo();// 进入主页面
			}
		});
		builder.show();
	}
	/**
	 * 
	 */
	private void register(){
		Intent intent = new Intent(this,UserResgisterActivity.class);
		startActivity(intent);
		finish();
	}

	/**
	 *  检查是否有新版本,如果有就升级
	 */
	private void checkUpdate() {
		new Thread() {

			@Override
			public void run() {
				Message msg = Message.obtain();
				
				try {
					URL url = new URL(Constants.UPDATEURL);
					// 联网
					HttpURLConnection conn = (HttpURLConnection) url
							.openConnection();
					// 设置请求方法
					conn.setRequestMethod("GET");
					conn.setConnectTimeout(4000);
					int code = conn.getResponseCode();
					if (code == 200) {
						// 联网成功
						InputStream is = conn.getInputStream();
						// 把流转换成字符串String
						String result = StreamUtil.readFromStream(is);
						Log.i(TAG, "联网成功 " + result);
						// JSON解析
						JSONObject obj = new JSONObject(result);
						Log.i(TAG, "is Success ??? ");
						version = (String) obj.get("version");
						Log.i(TAG, version);
						description = (String) obj.get("description");
						Log.i(TAG, description);
						apkurl = (String) obj.get("apkurl");
						Log.i(TAG, apkurl);
						appName = apkurl.substring(apkurl.lastIndexOf("/") + 1);
						// 校验是否有新版本
						if (getVersionName().equals(version)) {
							// 版本一致.没有新版本,进入主页面
							msg.what = ENTER_HOME;
						} else {
							// 有新版本,弹出一升级对话框
							msg.what = SHOW_UPDATE_DIALOG;
						}
					}
				} catch (MalformedURLException e) {
					msg.what = URL_ERROR;
					e.printStackTrace();
				} catch (IOException e) {
					msg.what = NETWORK_ERROR;
					e.printStackTrace();
				} catch (JSONException e) {
					msg.what = JSON_ERROR;
					e.printStackTrace();
				} finally {
					
					updateHandler.sendMessage(msg);
				}
			}
		}.start();
	}
	
	public void checkUserInfo(){
		User info  = helper.queryUser();
		if(info != null){
			LogUtils.i(TAG, info.toString());
		}
		if(info == null){
			new CheckThread().start();
		} else {
			try {
				Thread.sleep(Constants.DELAYTIME);
			} catch (InterruptedException e) {
			}
			enterHome();
		}
	}
	
	class CheckThread extends Thread{
		@Override
		public void run() {
			if(!Common.isNetworkConnected(SplashActivity.this)){
				Toast.makeText(SplashActivity.this, "亲!似乎没有连好网络耶", 0).show();
				SplashActivity.this.finish();
			}
			
			User u = null;
			Message msg = checkHandler.obtainMessage();
			String imei =Common.getDeviceId(SplashActivity.this);
			try {
				Response res = Api.getUserByImei(imei);
				LogUtils.i(TAG, "用户已经注册过了");
				msg.obj = res;
				msg.what = res.getEchoCode();
				
			} catch (Exception e) {
				LogUtils.e(TAG, e.getMessage());
				msg.what = Constants.INTERNET_ERROR;
			}
			long endTime = System.currentTimeMillis();// 结束的时间
			long dTime = endTime - startTime;// 算出花费的时间
			if (dTime < delayTime) {
				try {
					Thread.sleep(delayTime - dTime);
				} catch (InterruptedException e) {
				}
			}
			checkHandler.sendMessage(msg);
		}
	}

}
