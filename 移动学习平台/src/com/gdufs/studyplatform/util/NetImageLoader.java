package com.gdufs.studyplatform.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Formatter.BigDecimalLayoutForm;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;

import com.gdufs.studyplatform.api.Api;
import com.gdufs.studyplatform.bean.Response;
import com.gdufs.studyplatform.config.Constants;
import com.gdufs.studyplatform.util.NativeImageLoader.NativeImageCallBack;

public class NetImageLoader {
	private static final String TAG = "NetImageLoader";
	private static NetImageLoader mInstance = new NetImageLoader();
	private ExecutorService mImageThreadPool = Executors.newFixedThreadPool(1);
	private String picName;
	
	
	private NetImageLoader(){
		
	}
	
	/**
	 * 此方法来加载网络图片，这里的mPoint是用来封装ImageView的宽和高，我们会根据ImageView控件的大小来裁剪Bitmap
	 * 如果你不想裁剪年图片，调用loadNativeImage(final String path, final NativeImageCallBack mCallBack)来加载
	 * @param path
	 * @param mPoint
	 * @param mCallBack
	 * @return
	 */
	public Bitmap loadNetImage(final String path, final Point mPoint, final NetImageCallBack mCallBack){
		//先获取内存中的Bitmap
		
		final Handler mHander = new Handler(){

			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case Constants.IMAGE_GAIN_SUCCESS:
					//图片名称
					picName = path.substring(path.lastIndexOf("/") + 1);
					//拿到图片
					Bitmap bitmap = (Bitmap) msg.obj;
					//保存图片
					saveImage(bitmap);
					//先获取图片的缩微图
					bitmap = BitmapUtil.decodeThumbBitmapForFile(path,mPoint == null ? 0 : mPoint.x, mPoint == null ? 0 : mPoint.y);
					if(mCallBack != null){
						mCallBack.onImageLoader(bitmap, path);
					}
					break;
				case Constants.INTERNET_ERROR:
					
					break;
				case Constants.NO_REGISTERED:
					break;
				default:
					break;
				}
				
				mCallBack.onImageLoader((Bitmap)msg.obj, path);
			}
			
		};
		Bitmap bitmap = null;
		//若该Bitmap不在内存缓存中，则启用线程去加载网络的图片，并将Bitmap加入到mMemoryCache中
			mImageThreadPool.execute(new Runnable() {
				@Override
				public void run() {
					Message msg = mHander.obtainMessage();
					
					try {
						InputStream is = HttpUtil.getResource(Constants.IP_BASE + path);
						Bitmap bitmap = BitmapFactory.decodeStream(is);
						msg.what = Constants.IMAGE_GAIN_SUCCESS;
						msg.obj = bitmap;
						
						
					} catch (Exception e) {
						msg.what = Constants.INTERNET_ERROR;
						LogUtils.e(TAG, e.getMessage());
					}
					mHander.sendMessage(msg);
				}
			});
			return bitmap;
	}
	
	
	protected void saveImage( final Bitmap bitmap) {
		mImageThreadPool.execute(new Runnable() {
			
			@Override
			public void run() {
				File file = new File(Constants.IMAGE_DIR +"/" + picName);
				if(file.exists()){
					file.delete();
				}
				try {
					FileOutputStream fos = new FileOutputStream(file);
					bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
					fos.flush();
					fos.close();
					
				} catch (Exception e) {
				}
			}
		});
	}

	/**
	 * 通过此方法来获取NetImageLoader的实例
	 * @return
	 */
	public static NetImageLoader getInstance(){
		return mInstance;
	}
	
	public interface NetImageCallBack{
		/**
		 * 当子线程加载完了本地的图片，将Bitmap和图片路径回调在此方法中
		 * @param bitmap
		 * @param path
		 */
		public void onImageLoader(Bitmap bitmap,String path);
	}
	
}
