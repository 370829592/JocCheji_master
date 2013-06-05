package com.icalinks.mobile.ui;

import java.util.List;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.icalinks.jocyjt.R;
import com.icalinks.mobile.GlobalApplication;
import com.icalinks.mobile.ui.activity.AbsSubActivity;
import com.icalinks.obd.vo.ServicePhoneInfo;
import com.provider.model.resources.OBDHelper;

/**
 * 
* @ClassName: WarmActivity
* @Description: TODO(温馨服务)
* @author: wc_zhang@calinks.com.cn
* @date: 2013-5-23 下午7:14:27
*
 */
public class WarmActivity extends AbsSubActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.warm);
		
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		setTitle(getString(R.string.gd_wxfw));	
		initView();
		OBDHelper.getServicePhone(null);
	}
	
	private Button mBtnCall;

	private void initView() {
		showBackButton().setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				goback();
			}
		});
		mBtnCall = (Button) findViewById(R.id.warm_btn_call);
		mBtnCall.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				callServiceCenter();
			}
		});
	}

	private static final int PHONE_NUMBER_WARM = 0X99;

	private void callServiceCenter() {
		String phoneNumber = null;
		ServicePhoneInfo info = null;
		{
			List<ServicePhoneInfo> lstServicePhoneInfo = null;
			try {
				lstServicePhoneInfo = (List<ServicePhoneInfo>) GlobalApplication
						.getApplication().getObjPhoneNumber();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			if (lstServicePhoneInfo != null && lstServicePhoneInfo.size() > 0) {
				int size = lstServicePhoneInfo.size();
				for (int i = 0; i < size; i++) {
					info = lstServicePhoneInfo.get(i);
					if (info.getType() == PHONE_NUMBER_WARM) {
						break;
					}
				}
				phoneNumber = info.getSimcard();
			}
		}
		if (phoneNumber == null) {
			phoneNumber = getResources().getString(
					R.string.cfg_call_center_phone_number);
		}
		Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
				+ phoneNumber));

		WarmActivity.this.startActivity(intent);
	}


}
