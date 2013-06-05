package com.icalinks.mobile.ui;

import com.icalinks.jocyjt.R;
import com.icalinks.mobile.db.dal.UserDal;
import com.icalinks.mobile.util.DateTime;
import com.icalinks.obd.vo.VehicleInfo;
import com.provider.common.JocApplication;
import com.provider.model.Result;
import com.provider.model.resources.OBDHelper;
import com.provider.net.listener.OnCallbackListener;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

public class EditVehicleActivity extends BaseActivity {
	
	private Context mContext;
	private VehicleInfo edtVehicleInfo;
	private EditText edtLicenseplate, edtEngineno, edtFrameno, edtEmissions, edtLicenseDate, edtExamineDate;;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN , WindowManager.LayoutParams. FLAG_FULLSCREEN); 
		super.onCreate(savedInstanceState);
		
		mContext = this;
		setContentView(R.layout.info_vehicle_edit);
		
		initView(); 
		initData();
	}
	
    
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case R.id.rmct_carinf_edt_license_date:
			String sLicenseDate = edtLicenseDate.getText()+"";
			if(sLicenseDate.trim().equals("")){
				sLicenseDate = "2013-01-01";
			}
			sLicenseDate = sLicenseDate.trim();
			DateTime lDate = DateTime.from(sLicenseDate, "yyyy-MM-dd");
			return new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener(){

				@Override
				public void onDateSet(DatePicker view, int year,
						int month, int day) {
					String sDate = year + "-" + (month+1) + "-" + day;
					edtLicenseDate.setText(sDate);
				}
				
			}, lDate.getYear(), lDate.getMonth(), lDate.getDay());
			
		case R.id.rmct_carinf_edt_examine_date:
			String sExamineDate = edtExamineDate.getText()+"";
			if(sExamineDate.trim().equals("")){
				sExamineDate = "2013-01-01";
			}
			sExamineDate = sExamineDate.trim();
			DateTime eDate = DateTime.from(sExamineDate, "yyyy-MM-dd");
			return new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener(){

				@Override
				public void onDateSet(DatePicker view, int year,
						int month, int day) {
					String sDate = year + "-" + (month+1) + "-" + day;
					edtExamineDate.setText(sDate);
				}
				
			}, eDate.getYear(), eDate.getMonth(), eDate.getDay());
			
		}
		return super.onCreateDialog(id);
	}
	
	private void initView(){
		edtLicenseplate = (EditText) findViewById(R.id.rmct_carinf_edt_licenseplate);
		edtEngineno = (EditText) findViewById(R.id.rmct_carinf_edt_engineno);
		edtFrameno = (EditText) findViewById(R.id.rmct_carinf_edt_frameno);
		edtEmissions = (EditText) findViewById(R.id.rmct_carinf_edt_emissions);
		edtLicenseDate = (EditText) findViewById(R.id.rmct_carinf_edt_license_date);
		edtExamineDate = (EditText) findViewById(R.id.rmct_carinf_edt_examine_date);
		
		edtLicenseDate.setOnClickListener(mOnClickListener);
		edtExamineDate.setOnClickListener(mOnClickListener);
		findViewById(R.id.rmct_carinf_btn_submit).setOnClickListener(mOnClickListener);
		
	}
	
	private void initData() {
		VehicleInfo vehicleInfo = JocApplication.getVehicleInfo();
		if(JocApplication.isGpsLogin() && vehicleInfo != null){
			edtLicenseplate.setText(vehicleInfo.getLicensePlate());
			edtEngineno.setText(vehicleInfo.getEngineNumber());
			edtFrameno.setText(vehicleInfo.getFrameNumber());
			edtEmissions.setText(vehicleInfo.getDisplacement());
			edtLicenseDate.setText(vehicleInfo.getLicenseDate());
			edtExamineDate.setText(vehicleInfo.getExamineDate());
		}
	}
	
	
	private View.OnClickListener mOnClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			int id = v.getId();
			
			switch (id) {
			case R.id.rmct_carinf_edt_license_date:
				showDialog(R.id.rmct_carinf_edt_license_date);
				break;
			case R.id.rmct_carinf_edt_examine_date:
				showDialog(R.id.rmct_carinf_edt_examine_date);
				break;
			case R.id.rmct_carinf_btn_submit:
				String strLicenseplate = (edtLicenseplate.getText()+"").trim();
				String stredtEngineno = (edtEngineno.getText()+"").trim();
				String strFrameno = (edtFrameno.getText()+"").trim();
				String strEmissions = (edtEmissions.getText()+"").trim();
				String strLicenseDate = (edtLicenseDate.getText()+"").trim();
				String strExamineDate = (edtExamineDate.getText()+"").trim();
				
				if(strLicenseplate.trim().equals("")){
					Toast.makeText(mContext, "请输入车牌号码",Toast.LENGTH_SHORT).show();
					return;
				}
				if(stredtEngineno.trim().equals("")){
					Toast.makeText(mContext, "请输入发动机号",Toast.LENGTH_SHORT).show();
					return;
				}
				if(strFrameno.trim().equals("")){
					Toast.makeText(mContext, "请输入车架号",Toast.LENGTH_SHORT).show();
					return;
				}
				if(strEmissions.trim().equals("")){
					Toast.makeText(mContext, "请输入汽车排量",Toast.LENGTH_SHORT).show();
					return;
				}
				if(strLicenseDate.trim().equals("")){
					Toast.makeText(mContext, "请输入驾照日期",Toast.LENGTH_SHORT).show();
					return;
				}
				if(strExamineDate.trim().equals("")){
					Toast.makeText(mContext, "请输入车辆日期",Toast.LENGTH_SHORT).show();
					return;
				}
				edtVehicleInfo = new VehicleInfo();
				VehicleInfo oldVehicleInfo = JocApplication.getVehicleInfo();
				if(oldVehicleInfo != null){
					edtVehicleInfo = oldVehicleInfo;
					//edtVehicleInfo.setVid(oldVehicleInfo.getVid());
					//edtVehicleInfo.setPassword(oldVehicleInfo.getPassword());
					//edtVehicleInfo.setDriverId(oldVehicleInfo.getDriverId());
					
					edtVehicleInfo.setLicensePlate(strLicenseplate);
					edtVehicleInfo.setEngineNumber(stredtEngineno);
					edtVehicleInfo.setFrameNumber(strFrameno);
					edtVehicleInfo.setDisplacement(strEmissions);
					edtVehicleInfo.setLicenseDate(strLicenseDate);
					edtVehicleInfo.setExamineDate(strExamineDate);
					
					submit(edtVehicleInfo);
				}else {
					Toast.makeText(mContext, "提交失败", Toast.LENGTH_SHORT).show();
				}
				
				break;

			default:
				break;
			}
		}
	};
	
	private void submit(final VehicleInfo vehicleInfo) {
		showLoadingDialog();
		OBDHelper.editVehicleInfo(vehicleInfo, new OnCallbackListener() {
			
			@Override
			public void onSuccess(Result result) {
				JocApplication.setVehicleInfo(vehicleInfo);
				UserDal userDal = UserDal.getInstance(mContext);
				userDal.update(vehicleInfo.getLicensePlate(),
						vehicleInfo.getPassword(), vehicleInfo.getLoginName(),
						vehicleInfo.getVid());
				Toast.makeText(mContext, result.head.resMsg, Toast.LENGTH_SHORT).show();
				disLoadingDialog();
				finish();
			}
			
			@Override
			public void onFailure(Result result) {
				Toast.makeText(mContext, result.head.resMsg, Toast.LENGTH_SHORT).show();
				disLoadingDialog();
			}
		});
	}

}
