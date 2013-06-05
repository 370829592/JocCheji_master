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

import com.icalinks.common.MParams;
import com.icalinks.jocyjt.R;
import com.icalinks.mobile.util.MLogUtils;

/**
 * @ClassName: 类名称:DoctorMenu
 * @author 作者 E-mail: wc_zhang@calinks.com.cn
 * @Description: TODO 汽车医生九宫格菜单
 * @version 创建时间：2013-5-21 上午10:35:27
 */
public class DoctorMenu extends AbsSubActivity implements OnItemClickListener {
	private int[] logo = { R.drawable.logo_info_cat_rtdata,
			R.drawable.logo_info_cat_overhaul, R.drawable.logo_info_cat_run,
			R.drawable.logo_info_cat_consumption, R.drawable.logo_info_cat_tmps };

	private String[] buttonText = { "油耗数据", "爱车体检", "单次行驶记录", "消费记录", "胎压检测" };

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
		setTitle("汽车医生");
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

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		switch (position) {
		case 0:
			Intent intent0 = new Intent(this, DocYHSJ.class);
			startActivity(intent0);
			break;
		case 1:
			Intent intent1 = new Intent(this,DocACTJ.class);
			startActivity(intent1);
			break;
		case 2:
			Intent intent2 = new Intent(this,DocDCXSJL.class);
			startActivity(intent2);
			break;
		case 3:
			Intent intent3 = new Intent(this,DocXFJL.class);
			startActivity(intent3);
			break;
		case 4:
			Intent intent4 = new Intent(this,DocTYJC.class);
			startActivity(intent4);
			break;
		}
		
	}




}
