package com.gdufs.studyplatform.adapter;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gdufs.studyplatform.R;
import com.gdufs.studyplatform.base.BaseFragment;
import com.gdufs.studyplatform.bean.Question;
import com.gdufs.studyplatform.config.Constants;
import com.gdufs.studyplatform.ui.CustomImageView;
import com.gdufs.studyplatform.ui.CustomImageView.OnMeasureListener;
import com.gdufs.studyplatform.ui.CustomListView;
import com.gdufs.studyplatform.util.Common;
import com.gdufs.studyplatform.util.LogUtils;
import com.gdufs.studyplatform.util.NativeImageLoader;
import com.gdufs.studyplatform.util.NativeImageLoader.NativeImageCallBack;
import com.gdufs.studyplatform.util.NetImageLoader;
import com.gdufs.studyplatform.util.NetImageLoader.NetImageCallBack;

public class QuestionListViewAdapter extends BaseAdapter {
	private static final String TAG = "QuestionListViewAdapter";
	
	private Point mPoint  = new Point(0,0);//用来封装ImageView的宽和高的对象
	private Context context;
	private List<Question> dataList;
	private CustomListView lv_show;
	private boolean show = false;
	private BaseFragment fragment;
	private int q_id;
	
	
	
	public void setShow(boolean show) {
		this.show = show;
	}
	public int getQ_id() {
		return q_id;
	}
	public QuestionListViewAdapter(BaseFragment fragment,Context context,List<Question> dataList,CustomListView lv_show) {
		this.fragment = fragment;
		this.context = context;
		this.dataList = dataList;
		this.lv_show = lv_show;
	}
	public QuestionListViewAdapter(Context context,List<Question> dataList,CustomListView lv_show) {
		this.context = context;
		this.dataList = dataList;
		this.lv_show = lv_show;
	}
	
	public void refreshData(List<Question> newList){
		dataList.clear();
		dataList.addAll(newList);
		notifyDataSetChanged();
	}
	@Override
	public int getCount() {
		if(dataList == null)
		{
			return 0;
		}
		return dataList.size();
	}

	@Override
	public Object getItem(int position) {
		return dataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		final Question question = dataList.get(position);
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.listview_item_question, parent,false);
			holder = new ViewHolder();
			holder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
			holder.iv_image = (CustomImageView) convertView.findViewById(R.id.iv_image);
			holder.tv_recount = (TextView) convertView.findViewById(R.id.tv_recount);
			holder.tv_timeAgo = (TextView) convertView.findViewById(R.id.tv_timeAgo);
			holder.tv_nickname = (TextView) convertView.findViewById(R.id.tv_nickname);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.tv_content.setText(question.getContent());
		holder.tv_nickname.setText(question.getNickname());
		holder.tv_recount.setText(String.valueOf(question.getRecount()));
		
		holder.tv_recount.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(!show){
					show = true;
					fragment.showEditCommentView();
					q_id = question.getId();
				} else {
					show = false;
					fragment.dissCommentPanel();
				}
			}
		});
		holder.tv_timeAgo.setText(Common.howTimeAgo(context, Long.valueOf(question.getTimestamp())));
		
		if(question.getFileType().equals(Constants.PIC)){
			holder.iv_image.setVisibility(View.VISIBLE);
			//用来监听ImageView的宽和高
			holder.iv_image.setOnMeasureListener(new OnMeasureListener() {
				
				@Override
				public void onMeasureSize(int width, int height) {
					mPoint.set(width, height);
				}
			});
			
			int index = question.getFile().lastIndexOf("/");
			String picName = question.getFile().substring(index + 1);
			LogUtils.i(TAG, picName);
			final String image_path = Constants.IMAGE_DIR +"/" + picName;
			holder.iv_image.setTag(image_path);
			File file = new File(image_path);
			//如果本地有服務器上的就直接放上去,不必去服务器拿
			if(file.exists()){
				Bitmap bitmap = NativeImageLoader.getInstance().loadNativeImage(image_path,mPoint, new NativeImageCallBack() {
					
					@Override
					public void onImageLoader(Bitmap bitmap, String path) {
						CustomImageView iv = (CustomImageView) lv_show.findViewWithTag(image_path);
						if(bitmap != null&& iv != null){
							iv.setImageBitmap(bitmap);
						}
					}
				});
				if(bitmap != null){
					holder.iv_image.setImageBitmap(bitmap);
				}
			} 
			//只能去服务器拿洛
			else {
				NetImageLoader.getInstance().loadNetImage(question.getFile(), mPoint, new NetImageCallBack() {
					
					@Override
					public void onImageLoader(Bitmap bitmap, String path) {
						CustomImageView iv = holder.iv_image;
						if(bitmap != null){
							iv.setImageBitmap(bitmap);
						}
					}
				});
			}
		} else {
			holder.iv_image.setVisibility(View.GONE);
		}
		
		return convertView;
	}
	
	private class ViewHolder{
		TextView tv_content;
		CustomImageView iv_image;
		TextView tv_recount;
		TextView tv_timeAgo;
		TextView tv_nickname;
	}
}
