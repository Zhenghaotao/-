package com.gdufs.studyplatform.adapter;

import java.io.File;
import java.util.List;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gdufs.studyplatform.R;
import com.gdufs.studyplatform.bean.ResFile;
import com.gdufs.studyplatform.config.Constants;
import com.gdufs.studyplatform.util.Common;
import com.gdufs.studyplatform.util.LogUtils;

public class ResFileListViewAdapter extends BaseAdapter {
	
	private static final String TAG = "ResFileListViewAdapter";
	private Context context;
	private List<ResFile> list;
	
	
	private int[] iconIds = { R.drawable.no_downloading,// 还未下载的图标
			R.drawable.downloaded // 已经下载的图标
	};

	public void refreshData(List<ResFile> newData) {
		list.clear();
		list.addAll(newData);
		notifyDataSetChanged();
	}

	public ResFileListViewAdapter(Context context, List<ResFile> list) {
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
		final ResFile resFile = list.get(position);
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View
					.inflate(context, R.layout.listitem_resfile, null);
			holder.tv_filename = (TextView) convertView
					.findViewById(R.id.tv_filename);
			holder.tv_uploader = (TextView) convertView
					.findViewById(R.id.tv_uploader);
			holder.tv_uploadtime = (TextView) convertView
					.findViewById(R.id.tv_uploadtime);

			holder.iv_fileIcon = (ImageView) convertView
					.findViewById(R.id.iv_fileIcon);
			holder.btn_download = (Button) convertView
					.findViewById(R.id.btn_download);
			holder.tv_downloaded = (TextView) convertView
					.findViewById(R.id.tv_downloaded);
			holder.tv_process = (TextView) convertView
					.findViewById(R.id.tv_process);
			convertView.setTag(holder);
		}
		holder = (ViewHolder) convertView.getTag();
		final ViewHolder tempHolder = holder;
		holder.btn_download.setVisibility(View.GONE);
		holder.tv_downloaded.setVisibility(View.GONE);
		holder.tv_process.setVisibility(View.GONE);
		
		// 没有变化的控件
		holder.tv_filename.setText("文件名:" + resFile.getFilename());
		holder.tv_uploader.setText("上传者:" + resFile.getNickname());
		holder.tv_uploadtime.setText(Common.howTimeAgo(context,
				Long.valueOf(resFile.getTime())));

		holder.btn_download.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				tempHolder.tv_process.setVisibility(View.VISIBLE);
				
				// afnal
				FinalHttp finalhttp = new FinalHttp();
				LogUtils.i(TAG,  Constants.BASE_PATH + File.separator  + resFile.getFilename());
				finalhttp.download(Constants.IP_BASE + resFile.getUrl(), Constants.BASE_PATH + File.separator  + resFile.getFilename(), new AjaxCallBack<File>() {

					@Override
					public void onFailure(Throwable t, int errorNo,
							String strMsg) {
						super.onFailure(t, errorNo, strMsg);
						Toast.makeText(context,
								"下载失败", 1).show();
						tempHolder.tv_process.setVisibility(View.GONE);
					}

					@Override
					public void onLoading(long count, long current) {
						super.onLoading(count, current);
						int progress = (int) (current * 100 / count);
						tempHolder.tv_process.setText(progress + "%");
					}

					@Override
					public void onSuccess(File t) {
						super.onSuccess(t);
						tempHolder.tv_process.setVisibility(View.GONE);
						tempHolder.btn_download.setVisibility(View.GONE);
						notifyDataSetChanged();
					}
				});
			}
		});

		File file = new File(Constants.BASE_PATH + "/" + resFile.getFilename());
		if (file.exists()) {// 已经下载了
			holder.tv_downloaded.setVisibility(View.VISIBLE);
			holder.iv_fileIcon.setImageResource(iconIds[1]);
		} else {
			holder.iv_fileIcon.setImageResource(iconIds[0]);
			holder.btn_download.setVisibility(View.VISIBLE);
		}
		return convertView;
	}

	class ViewHolder {
		TextView tv_filename;
		TextView tv_uploader;
		TextView tv_uploadtime;
		// 有变化的控件
		Button btn_download;
		ImageView iv_fileIcon;
		TextView tv_downloaded;
		TextView tv_process;
	}
}
