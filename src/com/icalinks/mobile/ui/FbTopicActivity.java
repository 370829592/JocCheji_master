package com.icalinks.mobile.ui;

//
//import java.util.List;
//
//import android.app.ListActivity;
//import android.content.Intent;
//import android.os.Bundle;
//import android.os.Handler;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.ImageButton;
//import android.widget.ListView;
//import android.widget.TextView;
//
//import com.icalinks.jocyjt.R;
//import com.icalinks.mobile.GlobalApplication;
//import com.icalinks.mobile.db.dal.UserInfo;
//import com.icalinks.mobile.ui.adapter.MoreFbTopicAdapter;
//import com.icalinks.mobile.util.ToastShow;
//import com.icalinks.obd.vo.FeedbackInfo;
//import com.provider.model.Result;
//import com.provider.model.resources.OBDHelper;
//import com.provider.net.listener.OnCallbackListener;
//import com.umeng.fb.ui.FeedbackConversations;
//
//public class MoreFbTopicActivity extends ListActivity implements
//		OnClickListener, OnCallbackListener {
//	public void onCreate(Bundle arg0) {
//		super.onCreate(arg0);
//		setContentView(R.layout.umeng_fb_conversations);
//		initView();
//		initItem();
//	}
//
//	private ImageButton umeng_fb_imgBtn_add_facebook;
//	private MoreFbTopicAdapter mMoreFbTopicAdapter;
//
//	private void initView() {
//		umeng_fb_imgBtn_add_facebook = (ImageButton) findViewById(R.id.umeng_fb_imgBtn_add_facebook);
//		umeng_fb_imgBtn_add_facebook.setOnClickListener(this);
//		mMoreFbTopicAdapter = new MoreFbTopicAdapter(this);
//	}
//
//	@Override
//	protected void onListItemClick(ListView arg0, View arg1, int position,
//			long arg3) {
//		FeedbackInfo info = (FeedbackInfo) mMoreFbTopicAdapter
//				.getItem(position);
//		if (info.isReply())// 有回复时才打开
//		{
//			showFbReplyActivity(info.getFeedbackId());
//		}
//	}
//
//	private void showFbReplyActivity(String fbId) {
//		Intent intent = new Intent();
//		intent.setClass(this, MoreFbReplyActivity.class);
//		intent.putExtra(MoreFbReplyActivity.ARGS_FEEDBACK_ID, fbId);
//		startActivity(intent);
//	}
//
//	private void initItem() {
//		UserInfo info = GlobalApplication.getApplication().getCurrUser();
//		if (info != null) {
//			OBDHelper.getFeedbackInfoList(info.name, info.pswd, this);
//		} else {
//			ToastShow.show(this, "当前用户为空！");
//		}
//	}
//
//	@Override
//	public void onClick(View v) {
//		switch (v.getId()) {
//		case R.id.umeng_fb_imgBtn_add_facebook:
//			toFbStart();
//			break;
//		}
//	}
//
//	private void toFbStart() {
//		Intent intent = new Intent();
//		intent.setClass(this, MoreFbStartActivity.class);
//		startActivity(intent);
//		this.finish();
//	}
//
//	@Override
//	public void onFailure(Result arg0) {
//		mHandler.sendMessage(mHandler.obtainMessage(WHAT_FAILURE, arg0));
//	}
//
//	@Override
//	public void onSuccess(Result arg0) {
//		mHandler.sendMessage(mHandler.obtainMessage(WHAT_SUCCESS, arg0));
//	}
//
//	private static final int WHAT_FAILURE = 0;
//	private static final int WHAT_SUCCESS = 1;
//
//	private Handler mHandler = new Handler() {
//		public void handleMessage(android.os.Message msg) {
//			Result rlt = (Result) msg.obj;
//			switch (msg.what) {
//			case WHAT_FAILURE:
//				ToastShow.show(MoreFbTopicActivity.this, rlt.head.resMsg);
//				break;
//			case WHAT_SUCCESS:
//				if (rlt.object != null) {
//					List<FeedbackInfo> lstinfo = (List<FeedbackInfo>) rlt.object;
//					mMoreFbTopicAdapter.setItemList(lstinfo);
//					setListAdapter(mMoreFbTopicAdapter);
//				}
//				break;
//			}
//		};
//	};
// }
