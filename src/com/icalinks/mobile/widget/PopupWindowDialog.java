package com.icalinks.mobile.widget;

import android.app.Activity;
import android.app.Service;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;

public class PopupWindowDialog {
	public interface OnDestroyListener {
		public void onDestroy(PopupWindowDialog ppdialog);
	}

	private Activity mActivity;

	private PopupWindow mPopupWindow;
	private View mContentView;
	private boolean mIsShow;

	public PopupWindowDialog(Activity activity) {
		mActivity = activity;
	}

	public View getContentView() {
		return mContentView;
	}

	/**
	 * 设置PopupWindow要显示的内容View
	 */
	public void setContentView(int resId) {
		LayoutInflater inflater = (LayoutInflater) mActivity
				.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
		setContentView(inflater.inflate(resId, null));
	}

	/**
	 * 设置PopupWindow要显示的内容View
	 */
	public void setContentView(View contentView) {
		mContentView = contentView;
		mContentView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				float x = event.getX();
				float y = event.getY();
				if (x < mContentView.getLeft() || x > mContentView.getRight()
						|| y < mContentView.getTop()
						|| y > mContentView.getBottom()) {
					destroy();
					return mActivity.onTouchEvent(event);
				}
				return true;
			}
		});
	}

	public void showAtLocation(View parent) {
		showAtLocation(parent, Gravity.CENTER, 0, 0);
	}

	public void showAtLocation(View parent, int gravity, int x, int y) {

		if (mIsShow = !mIsShow) {
			// 显示
			check();
			mPopupWindow.showAtLocation(parent, gravity, x, y);

		} else {
			// 隐藏
			destroy();
		}
	}

	public void showAsDropDown(View anchor) {
		showAsDropDown(anchor, 0, 0);
	}

	public void showAsDropDown(View anchor, int xoff, int yoff) {
		if (mIsShow = !mIsShow) {
			check();
			// 显示
			mPopupWindow.showAsDropDown(anchor, xoff, yoff);
		} else {
			// 隐藏
			destroy();
		}
	}

	public void show() {
		mIsFullScreen = false;
		View parent = mActivity.getWindow().getDecorView();
		showAtLocation(parent, Gravity.CENTER, 0, 0);
	}

	private boolean mIsFullScreen;

	public void showFullScreen() {
		mIsFullScreen = true;
		show();
	}

	public void update() {
		mPopupWindow.update();
	}

	public void destroy() {// dismissDestroy
		mIsShow = false;
		if (null != mPopupWindow && mPopupWindow.isShowing()) {
			mPopupWindow.setFocusable(false);
			mPopupWindow.dismiss();
			mPopupWindow = null;
			if (mOnDestroyListener != null) {
				mOnDestroyListener.onDestroy(this);
			}
		}
	}

	private OnDestroyListener mOnDestroyListener;

	public void setOnDestroyListener(OnDestroyListener onDestroyListener) {
		this.mOnDestroyListener = onDestroyListener;
	}

	
	private void check() {
		if (mPopupWindow == null) {
			if (mIsFullScreen) {
				mPopupWindow = new PopupWindow(mContentView,
						LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			} else {
				mPopupWindow = new PopupWindow(mContentView,
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			}
			// mPopupWindow.setAnimationStyle(R.style.AnimationFade);
			mPopupWindow.setFocusable(true); // 设置PopupWindow可获得焦�?
			mPopupWindow.setTouchable(true); // 设置PopupWindow可触�?
			mPopupWindow.setOutsideTouchable(true); // 设置非PopupWindow区域可触�?在onTouch事件中根据触摸点是否在区域内来判�?
		}
	}
}
