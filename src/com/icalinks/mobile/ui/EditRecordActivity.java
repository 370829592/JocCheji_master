package com.icalinks.mobile.ui;

import java.util.ArrayList;
import java.util.List;

import com.icalinks.jocyjt.R;
import com.icalinks.mobile.ui.EditInsureActivity.InsureTypeInfo;
import com.icalinks.mobile.ui.adapter.ServiceTypeAdapter;
import com.icalinks.mobile.widget.CustomProgressDialog;
import com.icalinks.obd.vo.InsureType;
import com.icalinks.obd.vo.ServiceInfo;
import com.icalinks.obd.vo.ServiceType;
import com.icalinks.obd.vo.VehicleInfo;
import com.provider.common.JocApplication;
import com.provider.model.Result;
import com.provider.model.resources.OBDHelper;
import com.provider.net.listener.OnCallbackListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 编辑消费记录
 * @author Administrator
 *
 */
public class EditRecordActivity extends BaseActivity {
	
	private Context mContext;
	private ListView mTypeListView;
	private ServiceTypeAdapter mTypeAdapter;
	private List<ServiceTypeInfo> mServiceTypeList;
	private View vChargeWeiXiu, vChargeElse;
	private boolean isChargeWeiXiu, isChargeElse;
	private EditText edtCharge, edtDistance;
	private ServiceInfo mEditServiceInfo;
	private String mChargeType = "";
	private TextView tvServiceContent;
	
	private String strContent = "保养内容 2,保养内容 5";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN , WindowManager.LayoutParams. FLAG_FULLSCREEN); 
		
		super.onCreate(savedInstanceState);

		mContext = this;
		setContentView(R.layout.info_record_edit);
		
		mEditServiceInfo = (ServiceInfo) getIntent().getSerializableExtra(ServiceInfo.class.getSimpleName());  
		initView();
		initData();
	}

	private void initView() {
		tvServiceContent = (TextView) findViewById(R.id.info_service_tv_content);
		mTypeListView = (ListView) findViewById(R.id.info_service_type_list_view);
		vChargeWeiXiu = findViewById(R.id.info_service_btn_weixiu);
		vChargeElse = findViewById(R.id.info_service_btn_else);
		edtCharge = (EditText) findViewById(R.id.info_service_edt_benci);
		edtDistance = (EditText) findViewById(R.id.info_service_edt_gongli);
		
		findViewById(R.id.rmct_carinf_btn_submit).setOnClickListener(mOnClickListener);
		findViewById(R.id.rmct_carinf_rlt_weixiu).setOnClickListener(mOnClickListener);
		findViewById(R.id.rmct_carinf_rlt_weixiu).setOnTouchListener(mOnTouchListener);
		findViewById(R.id.rmct_carinf_rlt_else).setOnClickListener(mOnClickListener);
		findViewById(R.id.rmct_carinf_rlt_else).setOnTouchListener(mOnTouchListener);
		
	}
	
	private void initData() {
		if(mEditServiceInfo != null){
			edtCharge.setText(mEditServiceInfo.getPrice());
			edtDistance.setText(mEditServiceInfo.getDistance());
			String sChargeType= mEditServiceInfo.getChargeType();
			if(sChargeType != null){
				if(sChargeType.contains("2")){
					isChargeWeiXiu = true;
					vChargeWeiXiu.setPressed(false);
					vChargeWeiXiu.setSelected(isChargeWeiXiu);
				}
				if(sChargeType.contains("3")){
					isChargeElse = true;
					vChargeElse.setPressed(false);
					vChargeElse.setSelected(isChargeElse);
				}
			}
		}
		
		mServiceTypeList = new ArrayList<ServiceTypeInfo>();
		mTypeAdapter = new ServiceTypeAdapter(mContext);
		mTypeListView.setAdapter(mTypeAdapter);
		
//		for(int i=0; i<10; i++){
//			ServiceTypeInfo type = new ServiceTypeInfo();
//			type.setTypeId(i+"");
//			type.setTypeName("保养内容 " +i);
//			type.setSelected(strContent.contains(type.getTypeName()));
//
//			//mServiceTypeList.add(type);
//		}
		tvServiceContent.setText(mServiceTypeList.size()+"项  保养内容  可选择");
		mTypeAdapter.setDataList(mServiceTypeList);
		
		getServiceTypeFromServer();
	}
	
	View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			int id = v.getId();
			int eAction = event.getAction();
			if (eAction == MotionEvent.ACTION_DOWN) {
			}
			switch (id) {
			case R.id.rmct_carinf_rlt_weixiu:
				vChargeWeiXiu.setPressed(true);
				vChargeWeiXiu.setSelected(true);
				break;
			case R.id.rmct_carinf_rlt_else:
				vChargeElse.setPressed(true);
				vChargeElse.setSelected(true);
				break;

			default:
				break;
			}
			
			if (eAction == MotionEvent.ACTION_UP || eAction == MotionEvent.ACTION_CANCEL) {
				vChargeWeiXiu.setPressed(false);
				vChargeElse.setPressed(false);
			}
			return false;
		}
	};
    
	private View.OnClickListener mOnClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			int id = v.getId();
			
			switch (id) {
			case R.id.rmct_carinf_rlt_weixiu:
				vChargeWeiXiu.setPressed(false);
				vChargeWeiXiu.setSelected(!isChargeWeiXiu);
				isChargeWeiXiu = !isChargeWeiXiu;
				break;
			case R.id.rmct_carinf_rlt_else:
				vChargeElse.setPressed(false);
				vChargeElse.setSelected(!isChargeElse);
				isChargeElse = !isChargeElse;
				break;
			case R.id.rmct_carinf_btn_submit:
				String sChargeType = "";
				String strEdtChange = (edtCharge.getText()+"").trim();
				String strEdtDistance = (edtDistance.getText()+"").trim();
				if(strEdtChange.trim().equals("")){
					Toast.makeText(mContext, "请输入消费数额",Toast.LENGTH_SHORT).show();
					return;
				}
				if(strEdtDistance.trim().equals("")){
					Toast.makeText(mContext, "请输入保养公里数",Toast.LENGTH_SHORT).show();
					return;
				}
				StringBuffer sb = new StringBuffer("");
				for(ServiceTypeInfo typeInfo: mServiceTypeList){
					if(typeInfo.isSelected){
						sb.append(typeInfo.getTypeId());
						sb.append(";");
					}
				}
				String sbStr= sb.toString();
				if(sbStr.length() > 0){
					sChargeType = "1";
					sbStr = sbStr.substring(0, sbStr.length()-1);
				}
				if(sbStr.equals("") && !isChargeWeiXiu && !isChargeElse){
					Toast.makeText(mContext, "至少选择一项消费内容",Toast.LENGTH_SHORT).show();
					return;
				}
				ServiceInfo serviceInfo = new ServiceInfo();
				if(mEditServiceInfo != null){
					serviceInfo = mEditServiceInfo;
				}else {
					VehicleInfo vehicleInfo = JocApplication.getVehicleInfo();
					if(vehicleInfo != null){
						try {
							serviceInfo.setVid(Integer.valueOf(vehicleInfo.getVid()));
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				if(isChargeWeiXiu){
					if(sChargeType != null && sChargeType.length()>0){
						sChargeType = sChargeType + ";2";
					}else {
						sChargeType = "2";
					}
				}
				if(isChargeElse){
					if(sChargeType != null && sChargeType.length()>0){
						sChargeType = sChargeType + ";3";
					}else {
						sChargeType = "3";
					}
				}
				serviceInfo.setPrice(strEdtChange);
				serviceInfo.setDistance(strEdtDistance);
				serviceInfo.setTypeId(sbStr);
				serviceInfo.setChargeType(sChargeType);
				
				submit(serviceInfo);
				break;
			default:
				break;
			}
			
		}
	};
	

	private void submit(ServiceInfo serviceInfo) {
		showLoadingDialog();
		OBDHelper.editServiceInfo(serviceInfo, new OnCallbackListener() {
			
			@Override
			public void onSuccess(Result result) {
				Toast.makeText(mContext, result.head.resMsg, Toast.LENGTH_SHORT).show();
				disLoadingDialog();
				finish();
			}
			
			@Override
			public void onFailure(Result result) {
				disLoadingDialog();
				Toast.makeText(mContext, result.head.resMsg, Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	//获取保养内容列表
	private void getServiceTypeFromServer() {
		OBDHelper.getServiceTypeList(new OnCallbackListener() {	
			@SuppressWarnings("unchecked")
			@Override
			public void onSuccess(Result result) {
				if(result.head.resCode == 0){
					List<ServiceType> resDataList = (List<ServiceType>) result.object;
					if(resDataList != null && mEditServiceInfo != null){
						mServiceTypeList.clear();
						String strTypeName = mEditServiceInfo.getTypeName()+"";
						for(ServiceType type:resDataList){
							ServiceTypeInfo typeInfo = new ServiceTypeInfo();
							typeInfo.setTypeId(type.getTypeId());
							typeInfo.setTypeName(type.getTypeName());
							typeInfo.setSelected(strTypeName.contains(type.getTypeName()));
							mServiceTypeList.add(typeInfo);
						}
						tvServiceContent.setText(mServiceTypeList.size()+"项  保养内容  可选择");
						mTypeAdapter.notifyDataSetChanged();
					}
				}
			}
			
			@Override
			public void onFailure(Result result) {
				Toast.makeText(mContext, "获取保养内容列表失败", Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	public static class ServiceTypeInfo extends ServiceType {
		
		private static final long serialVersionUID = 1L;
		
		private boolean isSelected;

		public boolean isSelected() {
			return isSelected;
		}

		public void setSelected(boolean isSelected) {
			this.isSelected = isSelected;
		}
	}
	

}
