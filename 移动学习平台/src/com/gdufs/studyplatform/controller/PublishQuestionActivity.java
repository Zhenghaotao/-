package com.gdufs.studyplatform.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import net.tsz.afinal.exception.DbException;

import org.apache.http.client.ClientProtocolException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.gdufs.studyplatform.R;
import com.gdufs.studyplatform.api.Api;
import com.gdufs.studyplatform.bean.Question;
import com.gdufs.studyplatform.bean.Response;
import com.gdufs.studyplatform.bean.User;
import com.gdufs.studyplatform.config.Constants;
import com.gdufs.studyplatform.db.DBHelper;
import com.gdufs.studyplatform.util.LogUtils;

public class PublishQuestionActivity extends Activity implements
		OnClickListener {
	
	private static final String TAG = "PublishQuestionActivity";
	private Question question;
	private EditText et_content;
	private RelativeLayout rl_imageBox;
	private Button btn_publish;
	private ImageView iv_addImage;
	private File imageFile;
	private ProgressBar pb_publishing;
	
	
	private Handler publishHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			pb_publishing.setVisibility(View.GONE);
			btn_publish.setEnabled(true);
			btn_publish.setPressed(false);
			switch (msg.what) {
			case Constants.INTERNET_ERROR:
				Toast.makeText(PublishQuestionActivity.this, R.string.internet_error, Toast.LENGTH_LONG).show();
				break;
			case Constants.PUBLISH_SUCCESS:
				Toast.makeText(PublishQuestionActivity.this, "问题发布成功", Toast.LENGTH_LONG).show();
				if(imageFile != null){
					imageFile.delete();
				}
				Response res = (Response) msg.obj;
				Question ques = res.getQuestion();
				DBHelper dbHelper = new DBHelper(PublishQuestionActivity.this);
//				dbHelper.saveQuestion(ques);
				et_content.setText("");
				rl_imageBox.setVisibility(View.GONE);
			default:
				break;
			}
			
		};
	};

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_publish);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		initView();
	}

	private void initView() {
		pb_publishing = (ProgressBar) findViewById(R.id.pb_publishing);
		et_content = (EditText) findViewById(R.id.et_content);
		rl_imageBox = (RelativeLayout) findViewById(R.id.rl_imageBox);
		btn_publish = (Button) findViewById(R.id.btn_publish);
		iv_addImage = (ImageView) findViewById(R.id.iv_addImage);
		btn_publish.setOnClickListener(this);
		iv_addImage.setOnClickListener(this);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			Uri uri = data.getData();
			ContentResolver cr = this.getContentResolver();
			try {
				Bitmap bitmap = BitmapFactory.decodeStream(cr
						.openInputStream(uri));
				bitmap = compressImage(bitmap);

				savaBitmapToSDCard(bitmap);

				/* 将Bitmap设定到ImageView */
				((ImageView) findViewById(R.id.iv_Preview))
						.setImageBitmap(BitmapFactory.decodeFile(imageFile
								.getAbsolutePath()));
				
				rl_imageBox.setVisibility(View.VISIBLE);
			} catch (Exception e) {
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private Bitmap compressImage(Bitmap bitmap) {

		if (bitmap.getHeight() > 600) {
			bitmap = ThumbnailUtils.extractThumbnail(bitmap, bitmap.getWidth()
					* 600 / bitmap.getHeight(), 600);
		}
		return bitmap;

	}

	public void savaBitmapToSDCard(Bitmap bitmap) {
		if (imageFile != null) {
			imageFile.delete();
		}
		imageFile = new File(Constants.IMAGE_DIR + "/"
				+ System.currentTimeMillis());
		FileOutputStream fOut = null;
		try {
			imageFile.createNewFile();
			fOut = new FileOutputStream(imageFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);// 把Bitmap对象解析成流
		try {
			fOut.flush();
			fOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_publish:
			String content = et_content.getText().toString().trim();
			if (content.length() < 10) {
				Toast.makeText(this, "额,内容太少了,能详细点吗^_^#", Toast.LENGTH_SHORT)
						.show();
				return;
			}
			LogUtils.i(TAG, "开始作数据准备了,还有控件状态的变化");
			pb_publishing.setVisibility(View.VISIBLE);
			btn_publish.setEnabled(false);
			btn_publish.setPressed(true);
			
			question = new Question();
			question.setContent(content);
			User user = new DBHelper(PublishQuestionActivity.this).queryUser();
			question.setUserId(user.getId());
			question.setNickname(user.getNickname());
			if(imageFile != null && imageFile.exists()){
				question.setFileType(Constants.PIC);
				question.setFile(imageFile.getAbsolutePath());
			} else {
				question.setFileType(Constants.NO_PIC);
			}
			Toast.makeText(this, "正在发布....", Toast.LENGTH_SHORT).show();
			
			new PublishThread(question).start();
			
			break;
		case R.id.iv_addImage:
			Intent intent = new Intent();
			/* 开启Pictures画面Type设定为image */
			intent.setType("image/*");
			/* 使用Intent.ACTION_GET_CONTENT这个Action */
			intent.setAction(Intent.ACTION_GET_CONTENT);
			/* 取得相片后返回本画面 */
			startActivityForResult(intent, 1);
			break;

		}
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
	
	private class PublishThread extends Thread{
		private Question question;
		public PublishThread(Question question) {
			this.question = question;
		}
		@Override
		public void run() {
			Message msg = publishHandler.obtainMessage();
			try {
				Response res = Api.publicQuestion(question);
				msg.obj = res;
				msg.what  = res.getEchoCode();
			} catch (Exception e) {
				LogUtils.i(TAG, e.getMessage());
				msg.what = Constants.INTERNET_ERROR;
			}
			publishHandler.sendMessage(msg);
		}
	}
}
