package com.icalinks.mobile.ui.adapter;

import java.util.List;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.icalinks.jocyjt.R;
import com.icalinks.mobile.recver.ActionBarHelper;
import com.icalinks.mobile.ui.fragment.InfoRecordFragment;
import com.icalinks.obd.vo.ServiceInfo;
import com.provider.common.util.ToolsUtil;
import com.provider.model.Result;
import com.provider.model.resources.OBDHelper;
import com.provider.net.listener.OnCallbackListener;

/**
 * 保养记录列表适配器
 * 
 * @author zg_hu@icalinks.com.cn
 * 
 */

public class InfoRecordAdapter extends BaseAdapter {
	private InfoRecordFragment mContext;
	private Holder mHolder;
	private LayoutInflater mInflater;
	private List<ServiceInfo> mItemList;
	private boolean isShowBtnLayout;

	public InfoRecordAdapter(InfoRecordFragment context) {
		this.mContext = context;
		this.mInflater = (LayoutInflater) mContext.getActivitySafe()
				.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
	}

	public void setDataList(List<ServiceInfo> lstItem) {
		mItemList = lstItem;
	}

	public boolean isShowBtnLayout() {
		return isShowBtnLayout;
	}

	public void setShowBtnLayout(boolean isShowBtnLayout) {
		this.isShowBtnLayout = isShowBtnLayout;
	}

	public int getCount() {
		return mItemList.size();
	}

	public Object getItem(int position) {

		return mItemList.get(position);
	}

	public long getItemId(int position) {

		return position;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.info_record_item, null);
			convertView.setTag(mHolder = new Holder());
			{
				mHolder.date = (TextView) convertView
						.findViewById(R.id.info_record_item_date);
				mHolder.content = (TextView) convertView
						.findViewById(R.id.info_record_item_content);
				mHolder.tuition = (TextView) convertView
						.findViewById(R.id.info_record_item_tuition);
				mHolder.numofkm = (TextView) convertView
						.findViewById(R.id.info_record_item_numofkm);
				mHolder.btnNew = (Button) convertView
						.findViewById(R.id.info_new);
				mHolder.btnEdit = (Button) convertView
						.findViewById(R.id.info_edit);
				mHolder.btnDelete = (Button) convertView
						.findViewById(R.id.info_delete);
				mHolder.btnLayout = (View) convertView
						.findViewById(R.id.info_record_item_btn_layout);
			}
		} else {
			mHolder = (Holder) convertView.getTag();
		}

		final ServiceInfo item = mItemList.get(position);
		{
			String comStr = item.getTypeName()+"";
			mHolder.date.setText("          " +item.getServiceDate() +" 消费记录");
			if(comStr.length() > 60){
				comStr = comStr.substring(0, 60) + "......";
			}
			mHolder.content.setText(comStr);
			mHolder.tuition.setText(ToolsUtil.getData(item.getPrice())+" 元");
			mHolder.numofkm.setText(ToolsUtil.getData(item.getDistance()) +" 公里");
		}

		if (isShowBtnLayout) {
			mHolder.btnLayout.setVisibility(View.VISIBLE);
		} else {
			mHolder.btnLayout.setVisibility(View.GONE);
		}

		mHolder.btnNew.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				ServiceInfo newInfo = new ServiceInfo();
				newInfo.setVid(item.getVid());
				edit(newInfo);
			}
		});

		mHolder.btnEdit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				edit(item);
			}
		});

		mHolder.btnDelete.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				showLoadingDialog();
				OBDHelper.deleteServiceInfo(item, new OnCallbackListener() {

					@Override
					public void onSuccess(Result result) {
						mItemList.remove(position);
						notifyDataSetChanged();
						disLoadingDialog();
						Toast.makeText(mContext.getActivitySafe(), result.head.resMsg,
								Toast.LENGTH_SHORT).show();
						if(mItemList!=null && mItemList.size() == 0){
//							mContext.clearList();
						}
					}

					@Override
					public void onFailure(Result result) {
						disLoadingDialog();
						Toast.makeText(mContext.getActivitySafe(), result.head.resMsg,
								Toast.LENGTH_SHORT).show();
					}
				});
			}
		});

		return convertView;
	}

	public void edit(ServiceInfo serviceInfo) {
		Intent intent = new Intent();
		intent.setClass(mContext.getActivitySafe(),
				com.icalinks.mobile.ui.EditRecordActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable(ServiceInfo.class.getSimpleName(), serviceInfo);
		intent.putExtras(bundle);
		mContext.getActivitySafe().startActivity(intent);
	}

	private static class Holder {
		TextView date;
		TextView content;
		TextView tuition;
		TextView numofkm;
		Button btnNew;
		Button btnEdit;
		Button btnDelete;
		View btnLayout;
	}

	// //Loading...
	// private AlertDialog mLoadingDialog;
	public void showLoadingDialog() {
		// if (mLoadingDialog == null) {
		// mLoadingDialog = CustomProgressDialog.show(mContext);
		// } else if (!mLoadingDialog.isShowing()) {
		// mLoadingDialog.show();
		// }
		ActionBarHelper.startProgress();
	}

	public void disLoadingDialog() {
		// if (mLoadingDialog != null) {
		// mLoadingDialog.dismiss();
		// mLoadingDialog = null;
		// }
		ActionBarHelper.stopProgress();
	}
}
