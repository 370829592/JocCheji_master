package com.icalinks.mobile.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.icalinks.jocyjt.R;
import com.icalinks.mobile.GlobalApplication;
import com.icalinks.mobile.db.dal.UserInfo;
import com.icalinks.mobile.util.ToastShow;
import com.markupartist.android.widget.ActionBar;
import com.provider.model.Result;
import com.provider.model.resources.OBDHelper;
import com.provider.net.listener.OnCallbackListener;

/**
 * 安全围栏设置
* @ClassName: MoreFencesActivity
* @Description: TODO(这里用一句话描述这个类的作用)
* @author: wc_zhang@calinks.com.cn
* @date: 2013-5-24 下午2:29:54
*
 */
public class MoreFencesActivity extends BaseActivity implements
		OnCallbackListener {
	// private EditText mFences;
	private SeekBar mSeekBar;
	private TextView mTextView;
	private Context mContext;

	private ActionBar mActionBar;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.more_fences);

		mContext = getApplicationContext();
		mActionBar = (ActionBar) findViewById(R.id.more_fences_actionbar);
		mActionBar.setTitle(R.string.more_item_fences);
		Button back = mActionBar.showBackButton();
		back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				MoreFencesActivity.this.finish();
			}
		});

		Button save = mActionBar.showButton("保存");
		save.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				save();
			}
		});
		// findViewById(R.id.more_fences_btn_back).setOnClickListener(
		// mOnClickListener);
		// findViewById(R.id.more_fences_btn_save).setOnClickListener(
		// mOnClickListener);
		//
		// // mFences = (EditText) findViewById(R.id.more_fences_btn_radius);
		// // mFences.setText("" + getRmctRadius());
		// // mFences.setFocusable(true);
		mTextView = (TextView) findViewById(R.id.more_fences_lbl_radius);
		mSeekBar = (SeekBar) findViewById(R.id.more_fences_skb_radius);
		mSeekBar.setMax(5);
		mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				if (progress < 1)
					mSeekBar.setProgress(1);
				else
					mTextView.setText("当前半径:" + progress * 100 + "米");
			}
		});
		int radius = getRmctRadius();
		mSeekBar.setProgress(radius / 100);
		mTextView.setText("当前半径:" + radius + "米");
	}

	// private View.OnClickListener mOnClickListener = new
	// View.OnClickListener() {
	//
	// public void onClick(View v) {
	// switch (v.getId()) {
	// case R.id.more_fences_btn_back:
	// MoreFencesActivity.this.finish();
	// break;
	//
	// case R.id.more_fences_btn_save:save();
	// break;
	// default:
	// break;
	// }
	// }
	// };
	private void save() {

		// OBDHelper.setEnclosureRadius(arg0, arg1, arg2);
		if (!GlobalApplication.isGpsLogin()) {
			ToastShow.show(MoreFencesActivity.this,
					R.string.toast_not_logged_operation_alert);
			return;
		}

		UserInfo userinfo = GlobalApplication.getApplication().getCurrUser();
		if (userinfo != null) {
			int radius = mSeekBar.getProgress() * 100;
			showLoginDialog();
			OBDHelper.setEnclosureRadius(userinfo.name, userinfo.pswd, radius,
					MoreFencesActivity.this);
		}
	}

	public static final String RMCT_RADIUS_NAME = "rmct_radius_name";
	public static final String RMCT_RADIUS_KEY = "rmct_radius_key";
	public static final int RMCT_DEFAULT_RADIUS = 200;

	public int getRmctRadius() {
		SharedPreferences sharedPreferences = mContext.getSharedPreferences(
				RMCT_RADIUS_NAME, MODE_WORLD_READABLE);
		int rmctRadius = sharedPreferences.getInt(RMCT_RADIUS_KEY,
				RMCT_DEFAULT_RADIUS);// RMCT_PASSWORD_VALUE_DEFAULT
		return rmctRadius;
	}

	public void setRmctRadius(int radius) {
		SharedPreferences sharedPreferences = mContext.getSharedPreferences(
				RMCT_RADIUS_NAME, MODE_WORLD_READABLE);
		Editor editor = sharedPreferences.edit();
		editor.putInt(RMCT_RADIUS_KEY, radius);
		editor.commit();
	}

	public void onFailure(Result result) {
		hideLoginDialog();
		if ((result != null) && (result.head != null)) {
			if (result.head.resMsg != null) {
				ToastShow.show(this, result.head.resMsg);
			} else {
				ToastShow.show(this, "保存失败！");
			}
		}
	}

	public void onSuccess(Result arg0) {
		hideLoginDialog();
		setRmctRadius(mSeekBar.getProgress() * 100);
		Toast.makeText(MoreFencesActivity.this, "保存成功", Toast.LENGTH_LONG)
				.show();
		MoreFencesActivity.this.finish();
	}

	private void showLoginDialog() {
		mActionBar.setProgressBarVisibility(View.VISIBLE);
		// LayoutInflater inflater = LayoutInflater.from(this);
		// View layout = inflater.inflate(R.layout.login_loading, null);
		// AlertDialog.Builder builder = new AlertDialog.Builder(this);
		//
		// builder.setView(layout);
		//
		// mLoginDialog = builder.create();
		// mLoginDialog.show();
	}

	// private AlertDialog mLoginDialog;
	private void hideLoginDialog() {
		mActionBar.setProgressBarVisibility(View.GONE);
		// if (mLoginDialog != null) {
		// mLoginDialog.dismiss();
		// }
	}
}

// String strFences = mFences.getText().toString();
// try {
// int iFences = Integer.parseInt(strFences);
// if (iFences > 500 || iFences < 100) {
// Toast.makeText(MoreFencesActivity.this, "请输入100～500之间的数字",
// Toast.LENGTH_LONG).show();
// } else {
// setRmctRadius(iFences);
// Toast.makeText(MoreFencesActivity.this, "保存成功", Toast.LENGTH_LONG).show();
// MoreFencesActivity.this.finish();
// }
// } catch (Exception ex) {
// Toast.makeText(MoreFencesActivity.this, "输入的数字不争取，请重新输入！",
// Toast.LENGTH_LONG).show();
// }
