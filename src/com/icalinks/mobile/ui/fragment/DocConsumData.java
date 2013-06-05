package com.icalinks.mobile.ui.fragment;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.icalinks.jocyjt.R;
import com.icalinks.mobile.ui.activity.AbsSubActivity;


/**
 * @ClassName: 类名称:ConsumData
 * @author 作者 E-mail: wc_zhang@calinks.com.cn
 * @Description: TODO 油耗数据界面
 * @version 创建时间：2013-5-22 下午1:57:33
 */
public class DocConsumData extends BaseFragment {
	private View mContentView;
	
	private AbsSubActivity baseActivity;
	
	//行驶即时油耗 、行驶油耗数、 油耗费用
	private TextView tvInstantConsum, tvSingleConsum,tvConsumCost;
	
	//当次油耗值 、 总油耗 、 官方油耗
	private TextView tvCurrentConsum,tvTotalConsum,tvGuangfangConsum;
	//当次油耗值、 总油耗、 官方油耗 拉伸图片
	private ImageView ivCurrentConsum,ivTotalConsum,ivGuangfangConsum;

	private Typeface mTypeface;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mContentView = inflater.inflate(R.layout.doc_yhsj_layout, null);
		baseActivity = (AbsSubActivity) getActivitySafe();
		mTypeface = Typeface.createFromAsset(baseActivity.getAssets(), "fonts/lcd.ttf");
		setView();
		return mContentView;
	}
	
	private void setView(){
		tvInstantConsum = (TextView)mContentView.findViewById(R.id.tv_xsjsyh);
		tvSingleConsum = (TextView)mContentView.findViewById(R.id.tv_xsyhs);
		tvConsumCost = (TextView)mContentView.findViewById(R.id.tv_yhfy);
		
		tvCurrentConsum = (TextView)mContentView.findViewById(R.id.tv_current_youhao);
		tvTotalConsum = (TextView)mContentView.findViewById(R.id.tv_total_youhao);
		tvGuangfangConsum = (TextView)mContentView.findViewById(R.id.tv_guangfang_youhao);
		
		ivCurrentConsum = (ImageView)mContentView.findViewById(R.id.iv_current_youhao);
		ivTotalConsum = (ImageView)mContentView.findViewById(R.id.iv_total_youhao);
		ivGuangfangConsum = (ImageView)mContentView.findViewById(R.id.iv_guangfang_youhao);
		
		
		tvInstantConsum.setTypeface(mTypeface);
		tvSingleConsum.setTypeface(mTypeface);
		tvConsumCost.setTypeface(mTypeface);
	}
	
	
	private  void setListener(){
		showBackButton().setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				baseActivity.goback();
			}
		});
	}
	
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		setListener();
		setTitle("汽车医生-油耗数据");
		setCurrentConsum(20);
		setTotalConsum(30);
		setGuangfangConsum(100);
	}

	private void setCurrentConsum(int value){
		setImgWidth(ivCurrentConsum,value);
		tvCurrentConsum.setText(String.format("%.2f", value/10.0));
	}
	private void setTotalConsum(int value){
		setImgWidth(ivTotalConsum, value);
		tvTotalConsum.setText(String.format("%.2f", value/10.0));
	}
	private void setGuangfangConsum(int value){
		setImgWidth(ivGuangfangConsum, value);
		tvGuangfangConsum.setText(String.format("%.2f", value/10.0));
	}
	
	private void setImgWidth(ImageView v,int height){
		LayoutParams params = (LayoutParams) v.getLayoutParams();
		params.height = height;
		v.setLayoutParams(params);
		
	}
}
