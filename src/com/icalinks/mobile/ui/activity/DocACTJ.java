package com.icalinks.mobile.ui.activity;

import com.icalinks.mobile.ui.fragment.DocACTJFragment;
import com.icalinks.mobile.ui.fragment.DocConsumData;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

/**
 * @ClassName: 类名称:Doc_actj
 * @author 作者 E-mail: wc_zhang@calinks.com.cn
 * @Description: TODO
 * @version 创建时间：2013-6-4 下午5:58:45
 */
public class DocACTJ extends AbsSubActivity {
	private DocACTJFragment fragment;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		fragment = new DocACTJFragment();
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
