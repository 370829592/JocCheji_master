package com.icalinks.mobile.ui;

import java.util.Collections;
import java.util.List;

import android.app.ListActivity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.icalinks.common.AppsHelper;
import com.icalinks.common.NotifyHelper;
import com.icalinks.jocyjt.R;
import com.icalinks.mobile.GlobalApplication;
import com.icalinks.mobile.db.dal.MsgsDal;
import com.icalinks.mobile.db.dal.UserInfo;
import com.icalinks.mobile.ui.activity.AbsSubActivity;
import com.icalinks.mobile.ui.adapter.MsgsAdapter;
import com.icalinks.mobile.ui.model.MsgsItem;
import com.icalinks.mobile.ui.model.MsgsItemDateTimeComparator;
import com.icalinks.mobile.util.DateTime;
import com.icalinks.mobile.util.ToastShow;
import com.icalinks.obd.vo.FeedbackInfo;
import com.markupartist.android.widget.ActionBar;
import com.provider.model.Result;
import com.provider.model.resources.OBDHelper;
import com.provider.net.listener.OnCallbackListener;

/**
 * 
* @ClassName: MsgsActivity
* @Description: TODO(这里用一句话描述这个类的作用)信息中心
* @author: wc_zhang@calinks.com.cn
* @date: 2013-5-24 下午1:30:42
*
 */
public class MsgsActivity extends AbsSubActivity implements OnClickListener,
		OnCallbackListener {// , Runnable
	public void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.msgs);
		
		initView();
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		initItem();
		setTitle(getString(R.string.ms_xxzx));
		GlobalApplication.getApplication().getHomeActivity().showBackButton().setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				goback();
			}
		});
		// new Thread(this).start();// 标记已读
	}

	private List<MsgsItem> mMsgsItemList;
	private MsgsAdapter mMsgsAdapter;
	private ActionBar mActionBar;
	private Button mFeedback;
	private Button mViewback;

	private EditText mTxtFeedback;
	private Button mBtnSend;
	private ListView mListView;

	private void initView() {
		mActionBar =  GlobalApplication.getApplication().getHomeActivity().getmActionBar();
		mListView = (ListView)findViewById(R.id.msg_listview);
		mFeedback = mActionBar.showButton("历史");
		mFeedback.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showMsgsRecordActivity();
			}
		});

		mTxtFeedback = (EditText) findViewById(R.id.msgs_txt_feecback);
		mBtnSend = (Button) findViewById(R.id.msgs_btn_send);
		mBtnSend.setOnClickListener(this);
	}

	private UserInfo mUserInfo;

	private void initItem() {
		mUserInfo = GlobalApplication.getApplication().getCurrUser();
		if (mUserInfo != null) {
			mActionBar.setProgressBarVisibility(View.VISIBLE);
			{
				MsgsItem item = new MsgsItem();
				item.setVid(mUserInfo.getVid());
				item.setIsread(false);
				mMsgsItemList = MsgsDal.getInstance(this).select(item);
				Collections.sort(mMsgsItemList,
						new MsgsItemDateTimeComparator());// 时间顺序
				mMsgsAdapter = new MsgsAdapter(this);
				mMsgsAdapter.setItemList(mMsgsItemList);
				mListView.setAdapter(mMsgsAdapter);
				NotifyHelper.cancel(this);// 清除标题蓝提示

				markedRead();// 标记为已读
			}
			mActionBar.setProgressBarVisibility(View.GONE);
		} else {
			ToastShow.show(this, R.string.toast_not_logged_operation_alert);
		}
	}

	// @Override
	// protected void onListItemClick(ListView arg0, View arg1, int position,
	// long arg3) {
	// MsgsItem item = (MsgsItem) mMsgsAdapter.getItem(position);
	//
	// // 改未读为已读
	// if (!item.isIsread()) {
	// MsgsDal.getInstance(this).update(item.get_id());
	// item.setIsread(true);
	// }
	//
	// if (item.getMsgsType() == MsgsItem.MSGS_TYPE_FEEDBACK) {
	// showFbReplyActivity(item.getMsgsId());
	// }
	// mMsgsAdapter.notifyDataSetInvalidated();
	// }

	private void showMsgsRecordActivity() {
		Intent intent = new Intent();
		intent.setClass(this, MsgsRecordActivity.class);
		startActivity(intent);
	}

	// private void showFbReplyActivity(String fbId) {
	// Intent intent = new Intent();
	// intent.setClass(this, FbReplyActivity.class);
	// intent.putExtra(FbReplyActivity.ARGS_FEEDBACK_ID, fbId);
	// startActivity(intent);
	// }

	public void testNotify(View v) {
		NotifyHelper.notify(this, MoreAboutActivity.class, "这是一个测试",
				"你知道我这是在测试什么吗？吼吼吼吼");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.msgs_btn_send:
			sendFeedback();
			break;
		}
	}

	private FeedbackInfo mFeedbackInfo;

	private void sendFeedback() {

		if (mTxtFeedback.getText().toString().trim().equals("")) {
			ToastShow.show(this, R.string.toast_feedback_null);
			return;
		}

		// 当前用户
		if (mUserInfo != null) {
			mFeedbackInfo = new FeedbackInfo();
			{
				mFeedbackInfo.setLicenseplate(mUserInfo.name);
				mFeedbackInfo.setVid(mUserInfo.vid);

				// 反馈内容
				mFeedbackInfo.setComment("" + mTxtFeedback.getText());

				// 设备编号
				TelephonyManager tm = (TelephonyManager) getSystemService(Service.TELEPHONY_SERVICE);
				mFeedbackInfo.setDeviceId(tm.getDeviceId());

				// 手机号码
				mFeedbackInfo.setPhone(GlobalApplication.getPhoneNumber());

				// //* 客户端软件版本号 */
				mFeedbackInfo.setVersionCode(""
						+ AppsHelper.getVersionCode(this));
				// SDK及系统版本号//* 系统版本 */
				mFeedbackInfo.setSystemVersion(Build.VERSION.SDK + ","
						+ Build.VERSION.RELEASE);
				// 手机型号
				/* 客户端软件 ID */
				mFeedbackInfo.setSoftwareId(Build.MODEL);
			}
			mActionBar.setProgressBarVisibility(View.VISIBLE);
			OBDHelper.addFeedback(mFeedbackInfo, this);
		} else {
			ToastShow.show(this, R.string.toast_not_logged_operation_alert);
		}
	}

	@Override
	public void onFailure(Result arg0) {
		mHandler.sendMessage(mHandler.obtainMessage(WHAT_FAILURE,
				arg0.head.resMsg));
	}

	@Override
	public void onSuccess(Result arg0) {
		mHandler.sendMessage(mHandler.obtainMessage(WHAT_SUCCESS));
	}

	private static final int WHAT_FAILURE = 0;
	private static final int WHAT_SUCCESS = 1;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case WHAT_FAILURE:
				ToastShow.show(MsgsActivity.this, "" + msg.obj);
				mActionBar.setProgressBarVisibility(View.GONE);
				break;
			case WHAT_SUCCESS:
				saveFeedback2db();// 插入到数据库
				mTxtFeedback.setText("");// 清空编辑框
				mActionBar.setProgressBarVisibility(View.GONE);
				break;
			}
		};
	};

	private void saveFeedback2db() {
		MsgsItem item = new MsgsItem();
		{
			item.setVid(mUserInfo.getVid());
			item.setDatetime(DateTime.now().toString());
			item.setTitle("");
			item.setContent(mFeedbackInfo.getComment());
			item.setMsgsId("");
			item.setMsgsType(MsgsItem.MSGS_TYPE_LOCATION);
			item.setIsread(true);
		}

		// 判断不存在并添加
		// if (null == msgsDal.exists(item.getMsgsType(), item.getMsgsId())) {
		MsgsDal.getInstance(this).insert(item);// 添加到数据
		// }

		// 刷新当前列表
		mMsgsItemList.add(item);
		mMsgsAdapter.notifyDataSetInvalidated();

		// 关闭键盘显示
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(mTxtFeedback.getWindowToken(), 0);
	}

	// @Override
	// public void run() {
	// mHandler.sendMessage(mHandler.obtainMessage(WHAT_UPDATE_READED));
	// // MsgsItem item = null;
	// // if (mMsgsItemList != null && mMsgsItemList.size() > 0) {
	// // int size = mMsgsItemList.size();
	// // for (int i = 0; i < size; i++) {
	// // item = (MsgsItem) mMsgsItemList.get(i);
	// // // 改未读为已读
	// // if (!item.isIsread()) {
	// // MsgsDal.getInstance(this).update(item.get_id());
	// // item.setIsread(true);
	// // }
	// // }
	// // mMsgsAdapter.notifyDataSetInvalidated();
	// // }
	// }

	private void markedRead() {
		MsgsItem item = null;
		if (mMsgsItemList != null && mMsgsItemList.size() > 0) {
			int size = mMsgsItemList.size();
			for (int i = 0; i < size; i++) {
				item = (MsgsItem) mMsgsItemList.get(i);
				// 改未读为已读
				if (!item.isIsread()) {
					MsgsDal.getInstance(this).update(item.get_id());
					item.setIsread(true);
				}
			}
			mMsgsAdapter.notifyDataSetInvalidated();
		}
	}
}