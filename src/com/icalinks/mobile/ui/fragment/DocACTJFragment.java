package com.icalinks.mobile.ui.fragment;

import com.icalinks.jocyjt.R;
import com.icalinks.mobile.ui.activity.AbsSubActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @ClassName: 类名称:DocACTJFragment
 * @author 作者 E-mail: wc_zhang@calinks.com.cn
 * @Description: TODO 爱车体检界面
 * @version 创建时间：2013-5-24 上午11:49:37
 */
public class DocACTJFragment extends BaseFragment {
	private AbsSubActivity mActivity;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mActivity = (AbsSubActivity) getActivitySafe();
		return inflater.inflate(R.layout.main, null);
	}
	
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		setListener();
		setTitle(mActivity.getString(R.string.doc_actj));
	}
	
	private void setListener(){
		showBackButton().setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mActivity.goback();
			}
		});
	}
}
