package com.icalinks.mobile.ui.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.icalinks.mobile.ui.fragment.InfoCatTmpsFragment;

/**
 * @ClassName: 类名称:OilConsumData
 * @author 作者 E-mail: wc_zhang@calinks.com.cn
 * @Description: TODO
 * @version 创建时间：2013-6-4 下午5:54:21
 */
public class DocTYJC extends AbsSubActivity {
	
	private InfoCatTmpsFragment fragment;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		fragment = new InfoCatTmpsFragment();
		fragment.setActivity(this);
		LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
		View v = fragment.onCreateView(inflater, null, savedInstanceState);
		setContentView(v);
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		fragment.onResume();
	}
}
