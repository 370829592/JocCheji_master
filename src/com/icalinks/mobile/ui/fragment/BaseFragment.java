package com.icalinks.mobile.ui.fragment;

import android.app.Activity;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.widget.Button;
import android.widget.Toast;

import com.icalinks.mobile.GlobalApplication;
import com.icalinks.mobile.ui.activity.AbsSubActivity;
import com.provider.model.Result;
import com.provider.net.listener.OnCallbackListener;

public class BaseFragment extends Fragment implements OnCallbackListener {
	// private static TabActivity mMainActivity;
	//
	// public static TabActivity getMainActivity() {
	// return mMainActivity;
	// }
	//
	// public static void setMainActivity(TabActivity tabActivity) {
	// mMainActivity = tabActivity;
	// }

	private Activity mActivity;

	public Activity getActivitySafe() {
		if (getActivity() == null) {
			return mActivity;
		} else {
			return getActivity();
		}
	}

	public void setActivity(Activity mActivity) {
		this.mActivity = mActivity;
	}

	protected CharSequence mTitle;

	public BaseFragment() {
		mTitle = this.getClass().getSimpleName();
	}

	public BaseFragment(int resId) {
		mTitle = GlobalApplication.getApplication().getText(resId);
	}

	public void setTitle(int resId) {
		mTitle = GlobalApplication.getApplication().getText(resId);
	}

	public CharSequence getTitle() {
		return mTitle;
	}

	private static final int WHAT_FAILURE = 0;
	private static final int WHAT_SUCCESS = 1;
	public Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case WHAT_FAILURE:
				onHandlerFailure(msg.obj);
				break;
			case WHAT_SUCCESS:
				Object obj = null;
				try {
					if (msg.obj != null) {
						obj = ((Result) msg.obj).object;
					}
				} catch (Exception ex) {
				}
				onHandlerSuccess(obj);
				break;
			}

		};
	};

	protected void onHandlerFailure(Object obj) {
		String err = null;
		try {
			err = ((Result) obj).head.resMsg;
		} catch (Exception ex) {
		}
		Toast.makeText(getActivitySafe(), "" + err, Toast.LENGTH_SHORT).show();
	}

	protected void onHandlerSuccess(Object obj) {
		String text = this.getClass().getSimpleName() + ":网络操作成功！";
		Toast.makeText(getActivitySafe(), text, Toast.LENGTH_SHORT).show();
	}

	public void onFailure(Result result) {
		mHandler.sendMessage(mHandler.obtainMessage(WHAT_FAILURE, result));
	}

	public void onSuccess(Result result) {
		mHandler.sendMessage(mHandler.obtainMessage(WHAT_SUCCESS, result));
	}


	public void setTitle(String title) {
		((AbsSubActivity)getActivitySafe()).setTitle(title);
	}

	public Button showBackButton() {
		return GlobalApplication.getApplication().getHomeActivity()
				.showBackButton();
	}

	public void hideBackButton() {
		GlobalApplication.getApplication().getHomeActivity().hideBackButton();
	}

	public Button showRightButton(String text) {
		return GlobalApplication.getApplication().getHomeActivity()
				.showActionBarButton(text);
	}

	public void hideRightButton() {
		GlobalApplication.getApplication().getHomeActivity()
				.hideActionBarButton();
	}

	public void showProgress() {
		GlobalApplication.getApplication().getHomeActivity()
				.showActionBarProcess();
	}

	public void hideProgress() {
		GlobalApplication.getApplication().getHomeActivity()
				.hideActionBarProcess();
	}

	// //Loading...
	// private AlertDialog mLoadingDialog;
	// public void showLoadingDialog() {
	// if (mLoadingDialog == null) {
	// mLoadingDialog = CustomProgressDialog.show(mActivity);
	// } else if (!mLoadingDialog.isShowing()) {
	// mLoadingDialog.show();
	// }
	// }
	// public void disLoadingDialog() {
	// if (mLoadingDialog != null) {
	// mLoadingDialog.dismiss();
	// mLoadingDialog = null;
	// }
	// }
}
