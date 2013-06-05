package com.icalinks.mobile.ui;

import java.util.List;

import android.app.ListActivity;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.icalinks.jocyjt.R;
import com.icalinks.mobile.GlobalApplication;
import com.icalinks.mobile.db.dal.UserInfo;
import com.icalinks.mobile.ui.adapter.FbReplyAdapter;
import com.icalinks.mobile.util.ToastShow;
import com.icalinks.obd.vo.FeedbackInfo;
import com.icalinks.obd.vo.FeedbackReplyInfo;
import com.markupartist.android.widget.ActionBar;
import com.provider.model.Result;
import com.provider.model.resources.OBDHelper;
import com.provider.net.listener.OnCallbackListener;
import com.umeng.fb.ui.FeedbackConversation;

public class FbReplyActivity extends ListActivity implements OnClickListener,
		OnCallbackListener {
	public static final String ARGS_FEEDBACK_ID = "args_feedback_id";

	@Override
	public void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.fbreply);
		initView();
		initItem();
	}

	private EditText umeng_fb_editTxtFb;
	private Button umeng_fb_btnSendFb;
	private FbReplyAdapter mMoreFbReplyAdapter;
	private ActionBar mActionBar;
	private Button mBtnBack;

	private void initView() {
		mActionBar = (ActionBar) findViewById(R.id.fbreply_actionbar);
		mActionBar.setTitle("反馈回复");
		mBtnBack = mActionBar.showBackButton();
		mBtnBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				FbReplyActivity.this.finish();
			}
		});

		umeng_fb_editTxtFb = (EditText) findViewById(R.id.umeng_fb_editTxtFb);
		umeng_fb_btnSendFb = (Button) findViewById(R.id.umeng_fb_btnSendFb);
		umeng_fb_btnSendFb.setOnClickListener(this);
		mMoreFbReplyAdapter = new FbReplyAdapter(this);
	}

	private void showFbReplyActivity(String fbId) {
		Intent intent = new Intent();
		intent.setClass(this, FbReplyActivity.class);
		intent.putExtra(FbReplyActivity.ARGS_FEEDBACK_ID, fbId);
		startActivity(intent);
	}

	private String mFbId;

	private void initItem() {
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			mFbId = bundle.getString(ARGS_FEEDBACK_ID);
			if (mFbId != null && !mFbId.trim().equals(""))
				OBDHelper.getFeedbackReplyInfoList(mFbId, this);
			else
				ToastShow.show(this, "ARGS_FEEDBACK_ID 参数有误");
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.umeng_fb_btnSendFb:
			sendFeedback();
			break;
		}
	}

	private FeedbackInfo mFeedbackInfo;

	private void sendFeedback() {
		// ToastShow.show(this, text);
		mFeedbackInfo = new FeedbackInfo();
		{
			mFeedbackInfo.setReply(true);
			mFeedbackInfo.setFeedbackId(mFbId);

			// 反馈内容
			mFeedbackInfo.setComment("" + umeng_fb_editTxtFb.getText());

			// 设备编号
			TelephonyManager tm = (TelephonyManager) getSystemService(Service.TELEPHONY_SERVICE);
			mFeedbackInfo.setDeviceId(tm.getDeviceId());

			// 当前用户
			UserInfo userInfo = GlobalApplication.getApplication()
					.getCurrUser();
			if (userInfo != null) {
				mFeedbackInfo.setLicenseplate(userInfo.name);
			}

			// 手机号码
			mFeedbackInfo.setPhone(GlobalApplication.getPhoneNumber());

			// //* 客户端软件版本号 */
			mFeedbackInfo.setVersionCode("" + getVersionCode());
			// SDK及系统版本号//* 系统版本 */
			mFeedbackInfo.setSystemVersion(Build.VERSION.SDK + ","
					+ Build.VERSION.RELEASE);
			// 手机型号
			/* 客户端软件 ID */
			mFeedbackInfo.setSoftwareId(Build.MODEL);

			// 年龄
			// String strAge = "" + um_fb_age_spinner.getSelectedItem();
			// info.setAge(arg0);
			// info.setSex(arg0)

			// 性别
			// um_fb_gender_spinner.getSelectedItem();
		}

		mActionBar.setProgressBarVisibility(View.VISIBLE);
		mIsAddFeedback = true;
		OBDHelper.addFeedback(mFeedbackInfo, this);
	}

	private boolean mIsAddFeedback = false;

	@Override
	public void onFailure(Result arg0) {
		mHandler.sendMessage(mHandler.obtainMessage(WHAT_FAILURE, arg0));
	}

	@Override
	public void onSuccess(Result arg0) {
		if (mIsAddFeedback)
			mHandler.sendMessage(mHandler.obtainMessage(WHAT_ADD_SUCCESS, arg0));
		else
			mHandler.sendMessage(mHandler
					.obtainMessage(WHAT_LIST_SUCCESS, arg0));

	}

	private static final int WHAT_FAILURE = 0;
	private static final int WHAT_LIST_SUCCESS = 1;
	private static final int WHAT_ADD_SUCCESS = 2;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			Result rlt = (Result) msg.obj;
			switch (msg.what) {
			case WHAT_FAILURE:
				ToastShow.show(FbReplyActivity.this, rlt.head.resMsg);
				FbReplyActivity.this.finish();
				break;
			case WHAT_LIST_SUCCESS:
				if (rlt.object != null) {
					List<FeedbackReplyInfo> lstinfo = (List<FeedbackReplyInfo>) rlt.object;
					if (lstinfo.size() > 0) {
						mMoreFbReplyAdapter.setItemList(lstinfo);
						setListAdapter(mMoreFbReplyAdapter);
					} else {
						ToastShow.show(FbReplyActivity.this, "没有反馈!");
						FbReplyActivity.this.finish();
					}
				}
				break;
			case WHAT_ADD_SUCCESS:
				ToastShow.show(FbReplyActivity.this, "反馈成功!");
				// mMoreFbReplyAdapter.getItemList().add(mFeedbackInfo);
				// mMoreFbReplyAdapter.notifyDataSetChanged();
				break;
			}

			mActionBar.setProgressBarVisibility(View.GONE);
		};
	};

	/**
	 * 获取应用版本
	 * 
	 * @return
	 */
	private int getVersionCode() {
		PackageManager manager = getPackageManager();
		PackageInfo info = null;
		try {
			info = manager.getPackageInfo(getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return info.versionCode;
	}
}
