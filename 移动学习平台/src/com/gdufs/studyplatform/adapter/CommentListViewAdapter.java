package com.gdufs.studyplatform.adapter;

import java.util.List;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gdufs.studyplatform.R;
import com.gdufs.studyplatform.bean.Comment;
import com.gdufs.studyplatform.util.Common;

public class CommentListViewAdapter extends BaseAdapter {
	
	private Context context;
	private List<Comment> data;
	private int userId;//发表者的id
	public CommentListViewAdapter(Context context,List<Comment> data,int userId) {
		this.context = context;
		this.data = data;
		this.userId = userId;
	}
	public void refreshData(List<Comment> newData){
		data.clear();
		data.addAll(newData);
		notifyDataSetChanged();
	}
	@Override
	public int getCount() {
		if(data == null){
			return 0;
		}
		return data.size();
	}
	@Override
	public Object getItem(int position) {
		if(data != null){
			data.get(position);
		}
		return null;
	}
	@Override
	public long getItemId(int position) {
		return position;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Comment comment = data.get(position);
		ViewHolder holder;
		if(convertView == null){
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.listview_item_comment, parent, false);
			holder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
			holder.tv_nickname = (TextView) convertView.findViewById(R.id.tv_nickname);
			holder.iv_louzhu = (ImageView) convertView.findViewById(R.id.iv_louzhu);
			holder.tv_minuteAgo = (TextView) convertView.findViewById(R.id.tv_minuteAgo);
			holder.tv_rank = (TextView) convertView.findViewById(R.id.tv_rank);
			convertView.setTag(holder);
		}
		holder = (ViewHolder) convertView.getTag();
		
		if(userId == comment.getUserId()){
			holder.iv_louzhu.setVisibility(View.VISIBLE);
			convertView.setAnimation(AnimationUtils.loadAnimation(context, R.anim.appear));
		}
		holder.tv_content.setText(comment.getContent());
		int index = position + 1;
		holder.tv_rank.setText("第 " + index + " 楼");
		holder.tv_nickname.setText(comment.getNickname());
		holder.tv_minuteAgo.setText(Common.howTimeAgo(context, Long.valueOf(comment.getTimestamp())));
		return convertView;
	}
	private class ViewHolder{
		TextView tv_content;
		TextView tv_nickname;
		ImageView iv_louzhu;
		TextView tv_minuteAgo;
		TextView tv_rank;
	}
}
