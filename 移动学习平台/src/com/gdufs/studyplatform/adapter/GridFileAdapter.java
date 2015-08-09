package com.gdufs.studyplatform.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gdufs.studyplatform.R;
import com.gdufs.studyplatform.bean.FilePerate;

public class GridFileAdapter extends BaseAdapter {
	List<String> fileList =null;
	LayoutInflater flater = null;
	FilePerate filePerate = null;
	Context context = null;
	
	int fileNum = 0;//文件数
	int folderNum = 0;//目录数

	public GridFileAdapter(FilePerate filePerate,List<String> fileList,Context context) {
				flater = LayoutInflater.from(context);
				this.context = context;
				this.fileList = fileList;
				this.filePerate = filePerate;
	}

	@Override
	public int getCount() {
		if(fileList == null){
			return 0;
		}
		return fileList.size();
	}

	@Override
	public Object getItem(int position) {
		if(fileList == null){
			return null;
		}
		return fileList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		fileNum = filePerate.getFileNum();//获取文件个数
		folderNum = filePerate.getFolderNum();
		ViewHolder viewHolder;
		if(convertView == null){
			convertView = flater.inflate(R.layout.grid_file_item, null);
			viewHolder = new ViewHolder();
			viewHolder.image = (ImageView)convertView.findViewById(R.id.iv_fileIcon);
			viewHolder.title = (TextView)convertView.findViewById(R.id.tv_fileName);
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder)convertView.getTag();
		}
		if(position >= folderNum){
			viewHolder.image.setImageResource(R.drawable.file);//文件的图标
		}else{
			viewHolder.image.setImageResource(R.drawable.folder);//目录的图标
		}
		viewHolder.title.setText(fileList.get(position));//文件名
		return convertView;
	}
	private class ViewHolder {
		public ImageView image;
		public TextView title;
	}

}
