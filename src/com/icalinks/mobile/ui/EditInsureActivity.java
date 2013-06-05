package com.icalinks.mobile.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.jar.JarOutputStream;

import com.icalinks.jocyjt.R;
import com.icalinks.mobile.ui.adapter.InsureTypeAdapter;
import com.icalinks.mobile.util.DateTime;
import com.icalinks.obd.vo.InsureCompany;
import com.icalinks.obd.vo.InsureInfo;
import com.icalinks.obd.vo.InsureType;
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
import android.test.IsolatedContext;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class EditInsureActivity extends BaseActivity {
	
	private Context mContext;
	private ListView mTypeListView;
	private InsureTypeAdapter mTypeAdapter;
	private List<InsureTypeInfo> mInsureTypeList;
	private Spinner mCompanySpinner;
	private ArrayAdapter<String> mCompanySpinnerAdapter;
	
	private String[] mArrCompanyName;
	private HashMap<Integer, InsureCompany> mInsureCompanyMap;
	
	private TextView tvInsureContent;
	private EditText edtPolicyno, edtOwner, edtStartTime, edtEndTime, edtCompany, edtCompanyId;
	private InsureInfo mEditInsureInfo;
	private String mSeleteCompanyId = "";
	
	private String strContent = "保险内容 2,保险内容 5";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN , WindowManager.LayoutParams. FLAG_FULLSCREEN); 
		
		super.onCreate(savedInstanceState);

		mContext = this;
		setContentView(R.layout.info_insure_edit);
		
		mEditInsureInfo = (InsureInfo) getIntent().getSerializableExtra(InsureInfo.class.getSimpleName());

		initView();
		initData();
	}
	
	private void initView() {
		tvInsureContent = (TextView) findViewById(R.id.info_insure_tv_content);
		mTypeListView = (ListView) findViewById(R.id.info_insure_type_list_view);
		edtPolicyno = (EditText) findViewById(R.id.info_insure_edt_policyno);
		edtOwner = (EditText) findViewById(R.id.info_insure_edt_owner);
		edtStartTime = (EditText) findViewById(R.id.info_insure_edt_starttime);
		edtEndTime = (EditText) findViewById(R.id.info_insure_edt_endtime);
		edtCompany = (EditText) findViewById(R.id.info_insure_edt_company);
		edtCompanyId = (EditText) findViewById(R.id.info_insure_edt_company_id);
		
		edtStartTime.setOnClickListener(mOnClickListener);
		edtEndTime.setOnClickListener(mOnClickListener);
		edtCompany.setOnClickListener(mOnClickListener);
		findViewById(R.id.rmct_carinf_btn_submit).setOnClickListener(mOnClickListener);
		
		mCompanySpinner = (Spinner) findViewById(R.id.info_company_spinner);
		mCompanySpinner.setOnItemSelectedListener(new SpinnerSelectedListener());
		mCompanySpinner.setVisibility(View.VISIBLE);
		
	}
	
	private void initData() {
		String[] companyArr = new String[1];
		HashMap<Integer, InsureCompany> companyMap = new HashMap<Integer, InsureCompany>();
		
		if(mEditInsureInfo != null){
			edtPolicyno.setText(mEditInsureInfo.getPolicyNo());
			edtOwner.setText(mEditInsureInfo.getOwner());
			edtStartTime.setText(mEditInsureInfo.getStartTime());
			edtEndTime.setText(mEditInsureInfo.getEndTime());
			edtCompany.setText(mEditInsureInfo.getCompanyName());
			edtCompanyId.setText(mEditInsureInfo.getCompanyNameId());
			
			mSeleteCompanyId = mEditInsureInfo.getCompanyNameId();
			companyArr[0] = mEditInsureInfo.getCompanyName()!=null?mEditInsureInfo.getCompanyName():"";
			InsureCompany insureCompany = new InsureCompany();
			insureCompany.setCompanyName(mEditInsureInfo.getCompanyName());
			insureCompany.setCompanyNameId(mEditInsureInfo.getCompanyNameId());
			companyMap.put(0, insureCompany);
		}

		mInsureTypeList = new ArrayList<InsureTypeInfo>();
		mTypeAdapter = new InsureTypeAdapter(mContext);
		mTypeListView.setAdapter(mTypeAdapter);
		
		createCompanySpinner(companyArr, companyMap, 0);	
//		for(int i=0; i<10; i++){
//			InsureTypeInfo type = new InsureTypeInfo();
//			type.setTypeId(i+"");
//			type.setTypeName("保险内容 " +i);
//			type.setSelected(strContent.contains(type.getTypeName()));
//
//			//mInsureTypeList.add(type);
//		}
		tvInsureContent.setText(mInsureTypeList.size()+"项  投保内容  可选择");
		mTypeAdapter.setDataList(mInsureTypeList);
		
		getInsureTypeFromServer();
		getInsureCompanyFromServer();
	}
	
	private synchronized void createCompanySpinner(String[] companyArr, HashMap<Integer, InsureCompany> companyMap, int iSeleted) {
		mArrCompanyName = companyArr;
		mInsureCompanyMap = companyMap;
		mCompanySpinnerAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, mArrCompanyName);
		mCompanySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mCompanySpinner.setAdapter(mCompanySpinnerAdapter);
		if(companyArr.length>0 && companyArr.length>iSeleted){
			mCompanySpinner.setSelection(iSeleted);
		}
		mCompanySpinnerAdapter.notifyDataSetChanged();
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case R.id.info_insure_edt_starttime:
			String sStartTime = edtStartTime.getText()+"";
			if(sStartTime.trim().equals("")){
				sStartTime = "2013-01-01";
			}
			sStartTime = sStartTime.trim();
			DateTime lDate = DateTime.from(sStartTime, "yyyy-MM-dd");
			return new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener(){

				@Override
				public void onDateSet(DatePicker view, int year,
						int month, int day) {
					String sDate = year + "-" + (month+1) + "-" + day;
					edtStartTime.setText(sDate);
				}
				
			}, lDate.getYear(), lDate.getMonth(), lDate.getDay());
			
		case R.id.info_insure_edt_endtime:
			String sEndTime = edtEndTime.getText()+"";
			if(sEndTime.trim().equals("")){
				sEndTime = "2013-01-01";
			}
			sEndTime = sEndTime.trim();
			DateTime eDate = DateTime.from(sEndTime, "yyyy-MM-dd");
			return new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener(){

				@Override
				public void onDateSet(DatePicker view, int year,
						int month, int day) {
					String sDate = year + "-" + (month+1) + "-" + day;
					edtEndTime.setText(sDate);
				}
				
			}, eDate.getYear(), eDate.getMonth(), eDate.getDay());
			
		}
		return super.onCreateDialog(id);
	}
	
	private View.OnClickListener mOnClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			int id = v.getId();
			
			switch (id) {
			case R.id.info_insure_edt_starttime:
				showDialog(R.id.info_insure_edt_starttime);
				break;
			case R.id.info_insure_edt_endtime:
				showDialog(R.id.info_insure_edt_endtime);
				break;
			case R.id.info_insure_edt_company:
				
				break;
			case R.id.rmct_carinf_btn_submit:
				String strEdtPolicyno = (edtPolicyno.getText()+"").trim();
				String strEdtOwner = (edtOwner.getText()+"").trim();
				String strEdtStartTime = (edtStartTime.getText()+"").trim();
				String strEdtEndTime = (edtEndTime.getText()+"").trim();
				String strEdtCompany = (edtCompany.getText()+"").trim();
				String strEdtCompanyId = (edtCompanyId.getText()+"").trim();
				
				if(strEdtPolicyno.trim().equals("")){
					Toast.makeText(mContext, "请输入保单号",Toast.LENGTH_SHORT).show();
					return;
				}
				if(strEdtOwner.trim().equals("")){
					Toast.makeText(mContext, "请输入被保险人名称",Toast.LENGTH_SHORT).show();
					return;
				}
				if(strEdtStartTime.trim().equals("")){
					Toast.makeText(mContext, "请选择投保起始时间",Toast.LENGTH_SHORT).show();
					return;
				}
				if(strEdtEndTime.trim().equals("")){
					Toast.makeText(mContext, "请选择投保结束时间",Toast.LENGTH_SHORT).show();
					return;
				}
				if(strEdtCompanyId.trim().equals("")){
					Toast.makeText(mContext, "请选择投保公司名称",Toast.LENGTH_SHORT).show();
					return;
				}
				
				StringBuffer sb = new StringBuffer("");
				for(InsureTypeInfo typeInfo: mInsureTypeList){
					if(typeInfo.isSelected){
						sb.append(typeInfo.getTypeId());
						sb.append(";");
					}
				}
				String sbStr= sb.toString();
				if(sbStr.length() > 0){
					sbStr = sbStr.substring(0, sbStr.length()-1);
				}
				if(sbStr.trim().equals("")){
					Toast.makeText(mContext, "至少选择一个投保内容",Toast.LENGTH_SHORT).show();
					return;
				}
				
				InsureInfo insureInfo = new InsureInfo();
				insureInfo = mEditInsureInfo;
				VehicleInfo vehicleInfo = JocApplication.getVehicleInfo();
				if(vehicleInfo != null){
					insureInfo.setVid(Integer.valueOf(vehicleInfo.getVid()));
				}
				insureInfo.setPolicyNo(strEdtPolicyno);
				insureInfo.setOwner(strEdtOwner);
				insureInfo.setStartTime(strEdtStartTime);
				insureInfo.setEndTime(strEdtEndTime);
				insureInfo.setCompanyNameId(strEdtCompanyId);
				
				insureInfo.setTypeId(sbStr);
				
				submit(insureInfo);
				break;
			default:
				break;
			}
			
		}
	};
	
	private class SpinnerSelectedListener implements OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			InsureCompany company = mInsureCompanyMap.get(arg2);
			if(company != null){
				mSeleteCompanyId = company.getCompanyNameId();
				edtCompanyId.setText(company.getCompanyNameId());
				//edtCompany.setText(company.getCompanyName());
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			
		}
	}
	
	//获取投保内容列表
	private void getInsureTypeFromServer() {
		OBDHelper.getInsureTypeList(new OnCallbackListener() {
			
			@Override
			public void onSuccess(Result result) {
				if(result.head.resCode == 0){
					List<InsureType> resDataList = (List<InsureType>) result.object;
					if(resDataList != null && mEditInsureInfo != null){
						mInsureTypeList.clear();
						String strTypeName = mEditInsureInfo.getTypeName()+"";
						for(InsureType type:resDataList){
							InsureTypeInfo typeInfo = new InsureTypeInfo();
							typeInfo.setTypeId(type.getTypeId());
							typeInfo.setTypeName(type.getTypeName());
							typeInfo.setSelected(strTypeName.contains(type.getTypeName()));
							mInsureTypeList.add(typeInfo);
						}
						tvInsureContent.setText(mInsureTypeList.size()+"项  投保内容  可选择");
						mTypeAdapter.notifyDataSetChanged();
					}
				}
			}
			
			@Override
			public void onFailure(Result result) {
				Toast.makeText(mContext, "获取投保内容列表失败", Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	//获取投保公司列表
	private void getInsureCompanyFromServer() {
		OBDHelper.getInsureCompanyList(new OnCallbackListener() {
			
			@Override
			public void onSuccess(Result result) {
				if(result.head.resCode == 0){
					List<InsureCompany> resDataList = (List<InsureCompany>) result.object;
					if(resDataList != null){
						int i=0;
						int iSeleted = 0;
						int size = resDataList.size();
						String[] companyNameArr = new String[size];
						HashMap<Integer, InsureCompany> insureCompanyMap = new HashMap<Integer, InsureCompany>();
						
						for(InsureCompany company:resDataList){
							if(i<size){
								companyNameArr[i] = company.getCompanyName();
								insureCompanyMap.put(i, company);
								if(company.getCompanyNameId().equals(mSeleteCompanyId)){
									iSeleted = i;
								}
								i++;
							}
						}
						createCompanySpinner(companyNameArr, insureCompanyMap, iSeleted);
					}
				}
			}
			
			@Override
			public void onFailure(Result result) {
				Toast.makeText(mContext, "获取投保公司列表失败", Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	//编辑提交
	private void submit(InsureInfo insureInfo) {
		showLoadingDialog();
		OBDHelper.editInsureInfo(insureInfo, new OnCallbackListener() {
			
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
	
	public static class InsureTypeInfo extends InsureType {
		
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
