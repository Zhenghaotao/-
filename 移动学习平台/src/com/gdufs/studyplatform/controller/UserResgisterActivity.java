package com.gdufs.studyplatform.controller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.gdufs.studyplatform.R;
import com.gdufs.studyplatform.api.Api;
import com.gdufs.studyplatform.bean.Response;
import com.gdufs.studyplatform.bean.User;
import com.gdufs.studyplatform.config.Constants;
import com.gdufs.studyplatform.db.DBHelper;
import com.gdufs.studyplatform.util.Common;
import com.gdufs.studyplatform.util.LogUtils;

public class UserResgisterActivity extends Activity {
	private static final String TAG = "UserResgisterActivity";
	
	
	private ProgressBar pb_registing;
	private EditText et_nickname;
	private Button btn_register;
	
	
	private Handler reigsterHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			btn_register.setPressed(false);
			btn_register.setEnabled(true);
			pb_registing.setVisibility(View.GONE);
			switch (msg.what) {
			case Constants.INTERNET_ERROR:
				Toast.makeText(UserResgisterActivity.this, R.string.internet_error, Toast.LENGTH_SHORT).show();
				break;
			case Constants.REGISTER_REPEAT:
				Toast.makeText(UserResgisterActivity.this, "sorry,目测有人取了个相同的昵称,请改个更逼格点的吧", Toast.LENGTH_SHORT).show();
				et_nickname.setText("");
				break;
			case Constants.REGISTER_SUCCESS:
				Response res = (Response) msg.obj;
				User user = res.getUser();
				LogUtils.i(TAG, "注册成功,  " + user);
				new DBHelper(UserResgisterActivity.this).saveUser(user);
				Toast.makeText(UserResgisterActivity.this, "恭喜,注册成功,即将进入主界面", Toast.LENGTH_SHORT).show();
				enterMain();
				break;
			}
		};
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		
		initView();
		btn_register.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String nickname = et_nickname.getText().toString().trim();
				if(TextUtils.isEmpty(nickname)){
					Toast.makeText(UserResgisterActivity.this, "昵称不能为空,取个帅点的名字吧-_-#", Toast.LENGTH_SHORT).show();
					return;
				}
				btn_register.setPressed(false);
				btn_register.setEnabled(true);
				pb_registing.setVisibility(View.VISIBLE);
				User user = new User();
				user.setNickname(nickname);
				user.setImei(Common.getDeviceId(UserResgisterActivity.this));
				new RegisterThread(user).start();
			}
		});
	}
	protected void enterMain() {
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Intent intent = new Intent(this,MainActivity.class);
		startActivity(intent);
		finish();
	}
	private void initView() {
		pb_registing = (ProgressBar) findViewById(R.id.pb_registing);
		et_nickname = (EditText) findViewById(R.id.et_nickname);
		btn_register = (Button) findViewById(R.id.btn_register);
	}
	
	private class RegisterThread extends Thread{
		private User user;
		
		public RegisterThread(User user) {
			this.user = user;
		}
		
		@Override
		public void run() {
			Message msg = reigsterHandler.obtainMessage();
			try {
				Response res = Api.userRegister(user);
				int echoCode = res.getEchoCode();
				msg.what = echoCode;
				msg.obj = res;
			} catch (Exception e) {
				msg.what = Constants.INTERNET_ERROR;
			}
			reigsterHandler.sendMessage(msg);
			
		}
	}
}
