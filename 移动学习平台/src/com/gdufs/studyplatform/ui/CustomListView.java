package com.gdufs.studyplatform.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.gdufs.studyplatform.R;
import com.gdufs.studyplatform.util.Common;
import com.gdufs.studyplatform.util.LogUtils;

public class CustomListView extends ListView implements OnScrollListener {

	private static final String TAG = "CustomListView";

	private final static int RATIO = 3;
	private final static int RELEASE_TO_REFRESH = 0;
	private final static int PULL_TO_REFRESH = 1;
	private final static int REFRESHING = 2;
	private final static int DONE = 3;
	private final static int LOADING = 4;

	private final static int LOADING_MORE = 5;
	private final static int LOADING_MORE_DONE = 6;

	private Context context;
	private View header;
	private View footer;

	private RotateAnimation animation;
	private RotateAnimation reverseAnimation;
	private int firstItemIndex;
	private int startY;
	private int headerHeight;
	private int footerState;
	private int state;
	private OnRefreshListener refreshListener;
	private OnScrollPositionListener positionListener;
	private boolean isBack;
	private boolean isRecored;
	private boolean isRefreshable = false;
	private TextView tipsTextView;
	private TextView lastUpdatedTextView;
	private ImageView arrowImageView;
	private ImageView progressBar;
	private long lastRefreshTime = System.currentTimeMillis();

	public CustomListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		setOnScrollListener(this);
		init();
	}
	public CustomListView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	public CustomListView(Context context) {
		this(context, null);
	}
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (firstItemIndex == 0 && !isRecored) {
				isRecored = true;
				startY = (int) ev.getY();
				LogUtils.i(TAG, "手指按下去的位置");
			}
			if(positionListener != null){
				positionListener.backTopButtonFocuse();
			}
			break;
		case MotionEvent.ACTION_MOVE:
			int tempY = (int) ev.getY();
			if (firstItemIndex == 0 && !isRecored) {
				LogUtils.i(TAG, "在手指移动的记录下位置");
				isRecored = true;
				startY = tempY;
			}
			if (state != REFRESHING && isRecored && state != LOADING) {
				// 保证在设置padding的过程中，当前的位置一直是在head，否则如果当列表超出屏幕的话，当在上推的时候，列表会同时进行滚动
				// 可以松手去刷新了
				if (state == RELEASE_TO_REFRESH) {
					setSelection(0);
					// 往上推了，推到了屏幕足够掩盖head的程度，但是还没有推到全部掩盖的地步
					if (((tempY - startY) / RATIO < headerHeight)
							&& (tempY - startY) > 0) {
						state = PULL_TO_REFRESH;
						changeHeaderViewByState();
						LogUtils.i(TAG, "由松开刷新状态转变到下拉刷新状态");
					} else if (tempY - startY <= 0) {// 一下子推到顶了
						state = DONE;
						changeHeaderViewByState();
						LogUtils.i(TAG, "由松开刷新状态转变到done状态");
					}
					// 往下拉了，或者还没有上推到屏幕顶部掩盖head的地步
					else {
						// 不用进行特别的操作，只用更新paddingTop的值就行了
					}
				}
				// 还没有到达显示松开刷新的时候,DONE或者是PULL_TO_REFRESH状态
				if (state == PULL_TO_REFRESH) {
					setSelection(0);
					// 下拉到可以进入RELEASE_TO_REFRESH的状态
					if ((tempY - startY) / RATIO >= headerHeight) {
						state = RELEASE_TO_REFRESH;
						isBack = true;
						changeHeaderViewByState();
						LogUtils.i(TAG, "由done或者下拉刷新状态转变到松开刷新");
					}// 上推到顶了
					else if (tempY - startY <= 0) {
						state = DONE;
						changeHeaderViewByState();

						// /Log.v(TAG, "由DOne或者下拉刷新状态转变到done状态");
					}
				}
				// done状态下
				if (state == DONE) {
					if (tempY - startY > 0) {
						state = PULL_TO_REFRESH;
						changeHeaderViewByState();
					}
				}
				// 更新headView的size
				if (state == PULL_TO_REFRESH) {
					header.setPadding(0, -1 * headerHeight + (tempY - startY)
							/ RATIO, 0, 0);
				}
				// 更新headView 的paddingTop
				if (state == RELEASE_TO_REFRESH) {
					header.setPadding(0, (tempY - startY) / RATIO
							- headerHeight, 0, 0);
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			if (state != REFRESHING && state != LOADING) {
				if (state == DONE) {

				}
				if (state == PULL_TO_REFRESH) {
					state = DONE;
					changeHeaderViewByState();
					LogUtils.i(TAG, "由下拉刷新状态,到done状态");
				}

				if (state == RELEASE_TO_REFRESH) {
					state = REFRESHING;
					changeHeaderViewByState();
					onRefresh();
					LogUtils.i(TAG, "由松开刷新状态,到done状态");
				}
			}
			isRecored = false;
			isBack = false;
			if(positionListener != null){
				positionListener.backTopButtonBlur();
			}
			break;
		default:
			break;
		}

		return super.onTouchEvent(ev);
	}

	private void init() {
		header = LayoutInflater.from(context).inflate(R.layout.list_header,
				null);
		footer = LayoutInflater.from(context)
				.inflate(R.layout.listfooter, null);

		arrowImageView = (ImageView) header
				.findViewById(R.id.head_arrowImageView);
		progressBar = (ImageView) header.findViewById(R.id.head_progressBar);
		tipsTextView = (TextView) header.findViewById(R.id.head_tipsTextView);
		lastUpdatedTextView = (TextView) header.findViewById(R.id.head_lastUpdatedTextView);

		Common.measureView(header);
		headerHeight = header.getMeasuredHeight();

		addHeaderView(header, null, false);

		addFooterView(footer, null, false);
		footer.setVisibility(View.GONE);
		header.setPadding(0, -1 * headerHeight, 0, 0);

		animation = new RotateAnimation(0, -180, Animation.RELATIVE_TO_SELF,
				0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		animation.setInterpolator(new LinearInterpolator());
		animation.setDuration(250);
		animation.setFillAfter(true);

		reverseAnimation = new RotateAnimation(-180, 0,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		reverseAnimation.setInterpolator(new LinearInterpolator());
		reverseAnimation.setDuration(200);
		reverseAnimation.setFillAfter(true);

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		firstItemIndex = firstVisibleItem;
		if (firstItemIndex == 0) {
			if (positionListener != null) {
				positionListener.hideBackTopButton();
			}
		} else {
			if (positionListener != null) {
				positionListener.showBackTopButton();
			}
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
			// 判断是否滚动到底部
			int lastIndex = view.getLastVisiblePosition();
			int count = view.getCount();
			if (lastIndex == count - 1 || lastIndex == count - 2) {
				footer.setVisibility(View.VISIBLE);
				if (((lastIndex - 1) % 10 == 0 || lastIndex % 10 == 0)
						&& count > 2) {
					footer.findViewById(R.id.footer_progressBar).setVisibility(
							View.VISIBLE);
					footer.findViewById(R.id.footer_hint).setVisibility(
							View.GONE);
					if (footerState != LOADING_MORE) {
						refreshListener.onShowNextPage();
						footerState = LOADING_MORE;
					}
				} else {
					footer.findViewById(R.id.footer_progressBar).setVisibility(
							View.GONE);
					footer.findViewById(R.id.footer_hint).setVisibility(
							View.VISIBLE);
				}

			}
		}
	}

	public void setOnRefreshListener(OnRefreshListener refreshListener) {
		this.refreshListener = refreshListener;
		isRefreshable = true;
	}

	public void setOnScrollPositionListener(OnScrollPositionListener listener) {
		this.positionListener = listener;
	}

	public void doRefresh() {
		state = REFRESHING;
		changeHeaderViewByState();
		onRefresh();
	}

	private void changeHeaderViewByState() {
		lastUpdatedTextView.setText(context
				.getString(R.string.refresh_lasttime)
				+ Common.howTimeAgo(context, lastRefreshTime));
		if (isRefreshable) {
			switch (state) {
			case RELEASE_TO_REFRESH:
				arrowImageView.setVisibility(View.VISIBLE);
				progressBar.setVisibility(View.GONE);
				tipsTextView.setVisibility(View.VISIBLE);
				arrowImageView.clearAnimation();
				arrowImageView.startAnimation(animation);
				tipsTextView.setText(context.getString(R.string.refresh_release));
				break;
			case PULL_TO_REFRESH:
				progressBar.setVisibility(View.GONE);
				tipsTextView.setVisibility(View.VISIBLE);
				arrowImageView.clearAnimation();
				arrowImageView.setVisibility(View.VISIBLE);
				// 是由RELEASE_To_REFRESH状态转变来的
				if (isBack) {
					isBack = false;
					arrowImageView.clearAnimation();
					arrowImageView.startAnimation(reverseAnimation);
				}  
				//Log.v(TAG, "当前状态，下拉刷新");
				tipsTextView.setText(context.getString(R.string.refresh_pull));
				break;
			case REFRESHING:
				header.setPadding(0, 0, 0, 0);
				
				progressBar.setVisibility(View.VISIBLE);
				arrowImageView.clearAnimation();
				arrowImageView.setVisibility(View.GONE);
				//tipsTextview.setText(context.getString(R.string.loading));
				tipsTextView.setVisibility(View.GONE);
				lastUpdatedTextView.setVisibility(View.GONE);
				//Log.v(TAG, "当前状态,正在刷新...");
				break;
			case DONE:
				header.setPadding(0, -1 * headerHeight, 0, 0);
				
				progressBar.setVisibility(View.GONE);
				arrowImageView.clearAnimation();
				arrowImageView.setImageResource(R.drawable.arrow);
				tipsTextView.setText(context.getString(R.string.refresh_pull));
				tipsTextView.setVisibility(View.VISIBLE);
				lastUpdatedTextView.setVisibility(View.VISIBLE);
				//Log.v(TAG, "当前状态，done");
				break;
			default:
				break;
			}
		}
	}

	public void dismissHeader(){
		header.setVisibility(View.GONE);
	}
	public void refreshComplete() {
		state = DONE;
		lastUpdatedTextView.setText(context
				.getString(R.string.refresh_lasttime)
				+ context.getString(R.string.at_now));
		lastRefreshTime = System.currentTimeMillis();
		changeHeaderViewByState();
	}
	private void onRefresh() {
		if (refreshListener != null) {
			refreshListener.onRefresh();
		}
	}
	public void showMoreComplete() {
		footerState = LOADING_MORE_DONE;
	}
	public interface OnRefreshListener {
		public void onRefresh();
		public void onShowNextPage();
	}
	public interface OnScrollPositionListener {
		public void showBackTopButton();

		public void hideBackTopButton();

		public void backTopButtonFocuse();

		public void backTopButtonBlur();
	}
}
