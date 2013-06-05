package com.icalinks.mobile.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.icalinks.common.JocParameter;
import com.icalinks.jocyjt.R;
import com.icalinks.mobile.GlobalApplication;
import com.icalinks.mobile.db.dal.UserInfo;
import com.icalinks.mobile.ui.model.InfoTravleItem;
import com.icalinks.obd.vo.StatisticsInfo;
import com.icalinks.obd.vo.TravelInfo;
import com.icalinks.obd.vo.VehicleInfo;
import com.provider.model.Result;
import com.provider.model.resources.NetHelper;
import com.provider.model.resources.OBDHelper;
import com.provider.model.resources.User;
import com.provider.net.listener.OnCallbackListener;

public class InfoTravleRecordAdapter extends BaseAdapter {
	private Context mContext;
	private LayoutInflater mLayoutInflater;
	private static InfoTravleRecordAdapter infoTravleRecordAdapter;
	// StatisticsInfo statisticsInfo;
	// private ArrayList<StatisticsInfo> infoList;
	private ArrayList<InfoTravleItem> infoList;
	// private List<InfoTravleItem> infoTravleItem=new
	// ArrayList<InfoTravleItem>();
	private UserInfo userInfo;
	private Typeface lcdTypeface;
	private Typeface simkaiTypeface;
	// private boolean sdFlag;
	private ImageView delect_per;
	private StatisticsInfo mStatisticsInfo;
	private TravelInfo mTravelInfo = new TravelInfo();
	private String totalDate;
	private ArrayList<StatisticsInfo> sinfoList;
	private boolean isKejian;

	public InfoTravleRecordAdapter(Context context) {
		this.mContext = context;
		mLayoutInflater = LayoutInflater.from(mContext);

		lcdTypeface = Typeface.createFromAsset(mContext.getAssets(),
				"fonts/lcd.ttf");
		// sdFlag = GlobalApplication.sdFlag;
		// if (sdFlag) {
		// lcdTypeface = Typeface.createFromFile(JocParameter.LCD_FONTPATH);
		// simkaiTypeface = Typeface.createFromFile(JocParameter.LV_FONTPATH);
		// }
		userInfo = GlobalApplication.getApplication().getCurrUser();
		// mStatisticsInfo=new StatisticsInfo();
		// mTravelInfo=new TravelInfo();
		// infoTravleItem=new InfoTravleItem(mStatisticsInfo, mTravelInfo);

	}

	// public void setInfoList(ArrayList<StatisticsInfo> infoList) {
	// this.infoList = infoList;
	// }

	public void setInfoList(ArrayList<InfoTravleItem> infoList) {
		this.infoList = infoList;
	}

	public boolean isKejian() {
		return isKejian;
	}

	public void setIsKejian(boolean isKejian) {
		this.isKejian = isKejian;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return infoList.size();
	}

	@Override
	public Object getItem(int position) {
		
		return infoList.get(position);
	}

	@Override
	public long getItemId(int position) {
		
		return position;
	}

	private Holder mHolder;

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		if (convertView == null) {
			
			final InfoTravleItem iTravelInfo = infoList.get(position);

			convertView = mLayoutInflater.inflate(
					R.layout.info_ssdata_record_per_item, null);

			mHolder = new Holder();
			convertView.setTag(mHolder);
			mHolder.num = (TextView) convertView
					.findViewById(R.id.info_ssdata_record_item_num);

			mHolder.text = (TextView) convertView
					.findViewById(R.id.info_ssdata_record_item_);

			mHolder.startTime = (TextView) convertView
					.findViewById(R.id.info_ssdata_record_item_start_time);

			mHolder.endTime = (TextView) convertView
					.findViewById(R.id.info_ssdata_record_item_end_time);
			mHolder.delect_per = (ImageView) convertView
					.findViewById(R.id.info_ssdata_record_delect_nav);

			mHolder.delect_per.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					VehicleInfo vehicleInfo = GlobalApplication
							.getApplication().getVehicleInfo();
					if (vehicleInfo == null) {
						Toast.makeText(mContext, "删除失败", 0).show();
						return;
					}
					OBDHelper.deleteTravelInfo(vehicleInfo.getVid(),
							iTravelInfo.getTravelId(), iTravelInfo
									.getThisStartTime(), iTravelInfo
									.getThisEndTime(),
							new OnCallbackListener() {

								@Override
								public void onSuccess(Result arg0) {

									Toast.makeText(mContext, "删除成功", 0).show();
									infoList.remove(position);
									InfoTravleRecordAdapter.this
											.notifyDataSetChanged();

								}

								@Override
								public void onFailure(Result arg0) {
									Toast.makeText(mContext, "删除失败", 0).show();
								}
							});

				}
			});
			mHolder.startTime.setTypeface(lcdTypeface);
			mHolder.endTime.setTypeface(lcdTypeface);
		} else {
			mHolder = (Holder) convertView.getTag();
		}

		if (isKejian) {
			mHolder.delect_per.setVisibility(View.VISIBLE);
		} else {
			mHolder.delect_per.setVisibility(View.GONE);
		}
		
		mHolder.num.setText((position + 1) + "");
		mHolder.startTime.setText(infoList.get(position).getStartTime() + " 至 " +infoList.get(position).getEndTime());
		//mHolder.text.setText("至");
		//mHolder.endTime.setText(" " + infoList.get(position).getEndTime() + " ");
		return convertView;
	}

	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

	}

	private static class Holder {
		TextView num;
		TextView startTime;
		TextView text;
		TextView endTime;
		ImageView delect_per;
	}

}
