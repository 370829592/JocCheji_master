package com.icalinks.mobile.ui.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import com.icalinks.common.MParams;
import com.icalinks.jocyjt.R;
import com.icalinks.mobile.GlobalApplication;
import com.icalinks.mobile.ui.MoreTelephoneActivity;
import com.icalinks.mobile.ui.MsgsActivity;
import com.icalinks.mobile.ui.NearActivity;
import com.icalinks.mobile.ui.fragment.BaseFragment;
import com.icalinks.mobile.util.ToastShow;
import com.provider.common.util.JocData;
import com.provider.model.resources.CallCenterHelper;

/**
 * @ClassName: 类名称:SecretaryMenu
 * @author 作者 E-mail: wc_zhang@calinks.com.cn
 * @Description: TODO 汽车秘书九宫格菜单界面
 * @version 创建时间：2013-5-22 下午3:19:30
 */
public class SecretaryMenu extends AbsSubActivity implements OnItemClickListener {
	private int[] logo = { R.drawable.logo_svcs_nearby,
			R.drawable.logo_rmct_cat_info, R.drawable.logo_svcs_road_service,
			R.drawable.logo_svcs_instead_service, R.drawable.logo_more_about,
			R.drawable.logo_svcs_message, R.drawable.logo_rmct_insure };

	private String[] buttonText = { "查周边", "车辆信息", "道路救援", "代驾服务", "一键导航",
			"信息中心", "查询保险" };

	private GridView mGridView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.doctor_main_layout);
		setView();
		setListener();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		setTitle("汽车秘书");
		hideBackButton();
		hideProgress();
		hideRightButton();
	}

	private void setView() {
		mGridView = (GridView) findViewById(R.id.gridview_doctor_main);
		List<HashMap<String, Object>> buttonList = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < logo.length; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("itemImg", logo[i]);
			map.put("itemText", buttonText[i]);
			buttonList.add(map);
		}
		SimpleAdapter adapter = new SimpleAdapter(this, buttonList,
				R.layout.gridview_item, new String[] { "itemImg", "itemText" },
				new int[] { R.id.item_img, R.id.item_text });
		mGridView.setAdapter(adapter);
	}

	private void setListener() {
		mGridView.setOnItemClickListener(this);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		Intent intent = null;
		switch (position) {
		case 0:
			intent = new Intent(this, NearActivity.class);
			startActivity(intent);
			break;

		case 1:// 车辆信息
			intent = new Intent(this, MsCLXX.class);
			startActivity(intent);
			break;
		case 2:// 道路救援
			intent = call(8, "4001681680");
			if (intent != null) {
				startActivity(intent);
			}
			break;
		case 3:// 代驾服务
			intent = call(7, "4001681680");
			if (intent != null) {
				startActivity(intent);
			}
			
			break;
		case 4:// 一键导航
			intent = call(153, "4001681680");
			if (intent != null) {
				startActivity(intent);
			}
			break;
		case 5:// 信息中心
			intent = new Intent(this, MsgsActivity.class);
			startActivity(intent);
			break;
		case 6:
			intent = new Intent(this, MsBXJL.class);
			startActivity(intent);
			break;
		}
		
	}

	private Intent call(int callType, String callNumber) {
		if (checkPhoneNumber()) {
			CallCenterHelper mCallCenterHelper = new CallCenterHelper(
					GlobalApplication.getApplication().getHomeActivity());
			mCallCenterHelper.onCalling(callType, callNumber, null);
			return null;
		} else {
			ToastShow.show(this, "请先设置手机号码，再进行相关操作！");
			// 转到设置手机号码页面
			Intent intent = new Intent();
			intent.setClass(this, MoreTelephoneActivity.class);
			return intent;
		}
	}

	private boolean checkPhoneNumber() {
		String strPhoneNumber = GlobalApplication.getPhoneNumberFromShare(this);
		if (strPhoneNumber == null || strPhoneNumber.trim().equals("")) {
			return false;
		}
		return true;
	}


}
