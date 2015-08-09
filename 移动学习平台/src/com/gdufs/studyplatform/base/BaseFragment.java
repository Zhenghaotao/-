package com.gdufs.studyplatform.base;

import android.support.v4.app.Fragment;

public abstract class BaseFragment extends Fragment {
	public abstract void initView();
	public abstract void initData();
	public abstract void dissCommentPanel();
	public abstract void showEditCommentView();
}
