package com.gdufs.studyplatform.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gdufs.studyplatform.R;
import com.gdufs.studyplatform.bean.ResFile;
import com.gdufs.studyplatform.util.Common;

public class MyUploadFileListViewAdapter extends BaseAdapter{
	private Context context;
	private List<ResFile> list;
	
	public void refreshData(List<ResFile> newData) {
		list.clear();
		list.addAll(newData);
		notifyDataSetChanged();
	}
	
	public MyUploadFileListViewAdapter(Context context, List<ResFile> list) {
		super();
		this.context = context;
		this.list = list;
	}

	@Override
	public int getCount() {
		if (list != null) {
			return list.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		ResFile r = list.get(position);
		if(convertView == null){
			holder = new ViewHolder();
			convertView = View.inflate(context, R.layout.listitem_myfile, null);
			
			holder.tv_filename = (TextView) convertView.findViewById(R.id.tv_filename);
			holder.tv_uploadtime = (TextView) convertView.findViewById(R.id.tv_uploadtime);
			convertView.setTag(holder);
		}
		holder = (ViewHolder) convertView.getTag();
		holder.tv_filename.setText(r.getFilename());
		holder.tv_uploadtime.setText(Common.howTimeAgo(context, Long.valueOf(r.getTime())));
		return convertView;
	}
	private class ViewHolder {
		private TextView tv_filename;
		private TextView tv_uploadtime;
	}
	
}
