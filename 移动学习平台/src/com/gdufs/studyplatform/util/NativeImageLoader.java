package com.gdufs.studyplatform.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.LruCache;

public class NativeImageLoader {
	private LruCache<String , Bitmap> mMemoryCache;
	private static NativeImageLoader mInstance = new NativeImageLoader();
	private ExecutorService mImageThreadPool = Executors.newFixedThreadPool(1);
	
	
	private NativeImageLoader(){
		//获取应用程序的最大内存
		final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
		
		
		//用最大内存的 1 / 4 來存储图片
		final int cacheSize = maxMemory / 4;
		mMemoryCache = new LruCache<String, Bitmap>(cacheSize){
			//获取每张图片的大小
			@Override
			protected int sizeOf(String key, Bitmap bitmap) {
				return bitmap.getRowBytes() * bitmap.getHeight() / 1024;
			}
		};
	}
	
	public Bitmap loadNativeImage(final String path,final NativeImageCallBack mCallBack){
		return this.loadNativeImage(path,null, mCallBack);
	}
	
	/**
	 * 此方法来加载本地图片，这里的mPoint是用来封装ImageView的宽和高，我们会根据ImageView控件的大小来裁剪Bitmap
	 * 如果你不想裁剪图片，调用loadNativeImage(final String path, final NativeImageCallBack mCallBack)来加载
	 * @param path
	 * @param mPoint
	 * @param mCallBack
	 * @return
	 */
	public Bitmap loadNativeImage(final String path, final Point mPoint, final NativeImageCallBack mCallBack){
		//先获取内存中的Bitmap
		Bitmap bitmap = getBitmapFromMemCache(path);
		
		final Handler mHandler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				mCallBack.onImageLoader((Bitmap) msg.obj, path);
			}
		};
		if(bitmap == null){
			mImageThreadPool.execute(new Runnable() {
				@Override
				public void run() {
					//先获取图片的缩微图
					Bitmap mBitmap = BitmapUtil.decodeThumbBitmapForFile(path,mPoint == null ? 0 : mPoint.x, mPoint == null ? 0 : mPoint.y);
					Message msg = mHandler.obtainMessage();
					msg.obj = mBitmap;
					mHandler.sendMessage(msg);
					
					//将图片加入到内存缓存
					addBitmapToMemoryCache(path, mBitmap);
				}
			});
		}
		return bitmap;
	}
	

	public void addBitmapToMemoryCache(String key,Bitmap bitmap){
		if(getBitmapFromMemCache(key) == null && bitmap != null){
			mMemoryCache.put(key, bitmap);
		}
	}
	
	public Bitmap getBitmapFromMemCache(String key){
		Bitmap bitmap = mMemoryCache.get(key);
		return bitmap;
	}
	
	/**
	 * 通过此方法来获取NativeImageLoader的实例
	 * @return
	 */
	public static NativeImageLoader getInstance(){
		return mInstance;
	}
	
	public interface NativeImageCallBack{
		public void onImageLoader(Bitmap bitmap,String path);
	}
	
}
