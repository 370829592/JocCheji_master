package com.icalinks.mobile.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.icalinks.common.NotifyHelper;
import com.icalinks.mobile.db.dal.MsgsDal;
import com.icalinks.mobile.db.dal.UserInfo;
import com.icalinks.mobile.ui.model.MsgsItem;
import com.icalinks.obd.vo.PushInfo;
import com.provider.model.Result;
import com.provider.model.resources.OBDHelper;
import com.provider.net.listener.OnCallbackListener;

public class MsgsHelper {
	private static Context sContext;
	private static UserInfo sUserInfo;
	private static final int RETRY_COUNT = 3;
	private static int mCurrentRetryCount;

	public static void requestPushMessage(Context context, UserInfo user) {

		sContext = context;
		sUserInfo = user;
		mCurrentRetryCount++;
		OBDHelper.getPushInfoList(sUserInfo.vid, true,
				mOnCallbackListenerPushinfo);
		Log.e("MsgsHelper", "requestPushMessage,mCurrentRetryCount:"
				+ mCurrentRetryCount);
	}

	private static OnCallbackListener mOnCallbackListenerPushinfo = new OnCallbackListener() {
		@Override
		public void onFailure(Result arg0) {
			if (mCurrentRetryCount < RETRY_COUNT) {
				// 重复请求
				MsgsHelper.requestPushMessage(sContext, sUserInfo);
			} else {
				mCurrentRetryCount = 0;// 请求次数清零
			}
		}

		@Override
		public void onSuccess(Result arg0) {
			mCurrentRetryCount = 0;// 请求次数清零
			if (arg0.object != null) {
				List<PushInfo> lstinfo = (List<PushInfo>) arg0.object;
				if (lstinfo.size() > 0) {
					List<MsgsItem> lstItems = getMsgsList(lstinfo);// 转换成列表并插入到数据库中

					clearPushMessage(lstItems);// 更新服务器消息状态

					// 提示最后一条消息
					MsgsItem item = lstItems.get(0);
					NotifyHelper.notify(sContext, "您有新的消息", item.getContent());
				}
			}
		}
	};

	private static List<MsgsItem> getMsgsList(List<PushInfo> lstinfo) {
		int size = lstinfo.size();
		List<MsgsItem> lstItem = new ArrayList<MsgsItem>(size);
		PushInfo info = null;
		MsgsItem item = null;
		MsgsDal msgsDal = MsgsDal.getInstance(sContext);
		for (int i = 0; i < size; i++) {
			info = lstinfo.get(i);
			item = new MsgsItem();
			{
				item.setVid(sUserInfo.vid);
				item.setDatetime(info.getTime());
				item.setTitle(info.getTitle());
				item.setContent(info.getContent());
				item.setMsgsId("" + info.getId());
				item.setMsgsType(info.getType());
				item.setIsread(false);
			}
			lstItem.add(item);

			// 判断不存在并添加
			if (null == msgsDal.exists(item.getVid(), item.getMsgsType(),
					item.getMsgsId())) {
				msgsDal.insert(item);// 添加到数据
			}
		}
		return lstItem;
	}

	public static void clearPushMessage(List<MsgsItem> lstItem) {
		int size = lstItem.size();
		MsgsItem itemtemp = null;
		List<String> lstIds = null;
		for (int i = 0; i < size; i++) {
			itemtemp = lstItem.get(i);
			lstIds = new ArrayList<String>(1);
			try {
				lstIds.add(lstItem.get(i).getMsgsId());
				OBDHelper.updatePushState(sUserInfo.vid,
						itemtemp.getMsgsType(), lstIds, null);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	// private static OnCallbackListener mOnCallbackListener = new
	// OnCallbackListener() {
	//
	// @Override
	// public void onSuccess(Result arg0) {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// @Override
	// public void onFailure(Result arg0) {
	// // TODO Auto-generated method stub
	//
	// }
	// };
}

// if (sMsgsItemList.size() > size) {
// Collections.sort(sMsgsItemList, new MsgsItemDateTimeComparator());
// }
// private static OnCallbackListener mOnCallbackListenerFeedback = new
// OnCallbackListener() {
// @Override
// public void onFailure(Result arg0) {
// sRequestCount--;
// mHandler.sendMessage(mHandler.obtainMessage(WHAT_FAILURE_FEEDBACK,
// arg0));
// }
//
// @Override
// public void onSuccess(Result arg0) {
// sRequestCount--;
// if (arg0.object != null) {
// fillFeedback2MsgsList((List<FeedbackInfo>) arg0.object);
// areNewMessage();
// }
// }
// };
// private static List<MsgsItem> sMsgsItemList;

// public static List<MsgsItem> getMsgsItemList() {
// return sMsgsItemList;
// }

// private static void areNewMessage() {
// if (sRequestCount == 0) {
// PushInfo info = new PushInfo();
// // info.
//
// // if (sOnMsgsListener != null)
// // sOnMsgsListener.onNewMsg();
// }
// }

// private static void fillFeedback2MsgsList(List<FeedbackInfo> lstinfo) {
// int size = lstinfo.size();
// FeedbackInfo info = null;
// MsgsItem item = null;
// for (int i = 0; i < size; i++) {
// info = lstinfo.get(i);
// item = new MsgsItem(MsgsItem.MSGS_TYPE_FEEDBACK);
// {
// item.setDatetime(info.getTime());
// item.setTitle(info.getComment());
// item.setId(info.getFeedbackId());
// item.setIsread(true);
// }
// sMsgsItemList.add(item);
// }
// if (sMsgsItemList.size() > size) {
// Collections.sort(sMsgsItemList, new MsgsItemDateTimeComparator());
// }
// }

// UserInfo user = GlobalApplication.getApplication().getCurrUser();
// if (user != null) {
//
// } else {
// ToastShow.show(sContext, "当前用户为空！");
// }
// private static final int WHAT_FAILURE_FEEDBACK = 0x0001;
// private static final int WHAT_SUCCESS_FEEDBACK = 0x1001;
// private static final int WHAT_FAILURE_PUSHINFO = 0x0002;
// private static final int WHAT_SUCCESS_PUSHINFO = 0x1002;
//
// private Handler mHandler = new Handler() {
// public void handleMessage(android.os.Message msg) {
// Result rlt = (Result) msg.obj;
// switch (msg.what) {
// case WHAT_FAILURE_FEEDBACK:
// case WHAT_FAILURE_PUSHINFO:
// ToastShow.show(sContext, rlt.head.resMsg);
// break;
// case WHAT_SUCCESS_FEEDBACK:
// if (rlt.object != null) {
// fillFeedback2MsgsList((List<FeedbackInfo>) rlt.object);
// // mMsgsAdapter.notifyDataSetChanged();
// }
// break;
//
// case WHAT_SUCCESS_PUSHINFO:
// if (rlt.object != null) {
// fillPushInfo2MsgsList((List<PushInfo>) rlt.object);
// // mMsgsAdapter.notifyDataSetChanged();
// }
// break;
// }
// };
// };

// OBDHelper.getFeedbackInfoList(user.name, user.pswd,
// mOnCallbackListenerFeedback);
// sRequestCount++;