package com.icalinks.mobile.ui.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import com.icalinks.jocyjt.R;
import com.icalinks.mobile.ui.MoreAboutActivity;
import com.icalinks.mobile.ui.MoreActMgrActivity;
import com.icalinks.mobile.ui.MoreFourSNumber;
import com.icalinks.mobile.ui.MoreTelephoneActivity;
import com.icalinks.mobile.ui.MoreYoujiaSetting;
import com.icalinks.mobile.ui.WarmActivity;

/**
 * 
* @ClassName: MoreMenu
* @Description: TODO(更多界面)
* @author: wc_zhang@calinks.com.cn
* @date: 2013-5-23 下午5:22:18
*
 */
public class MoreMenu extends AbsSubActivity implements OnItemClickListener{
	private int[] logo = { 
			R.drawable.more_cheliang_zhanghao,
			R.drawable.more_4sdianhuashezhi, R.drawable.more_benjihaomashezhi,
			R.drawable.more_youjiashezhi,R.drawable.more_wenxin_fuwu,
			R.drawable.more_about};


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
		setTitle("更  多");
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
			buttonList.add(map);
		}
		SimpleAdapter adapter = new SimpleAdapter(this, buttonList,
				R.layout.more_item, new String[] { "itemImg" },
				new int[] { R.id.more_item_imageview, });
		mGridView.setAdapter(adapter);
	}

	private void setListener() {
		mGridView.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		Intent intent = null;
		switch (position) {
		case 0://车辆账户管理
			intent = new Intent(this, MoreActMgrActivity.class);
			break;
		case 1:
			intent = new Intent(this, MoreFourSNumber.class);
			break;
		case 2:
			intent = new Intent(this, MoreTelephoneActivity.class);
			break;
		case 3:
			intent = new Intent(this, MoreYoujiaSetting.class);
			break;
		case 4:
			intent = new Intent(this, WarmActivity.class);
			break;
		case 5://关于
			intent = new Intent(this,MoreAboutActivity.class);
			break;
		}
		if(intent == null)return;
		startActivity(intent);
	}




}
