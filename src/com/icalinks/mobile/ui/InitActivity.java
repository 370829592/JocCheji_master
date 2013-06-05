package com.icalinks.mobile.ui;

import java.util.ArrayList;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.icalinks.jocyjt.R;
import com.icalinks.mobile.ui.activity.MainActivityGroup;

public class InitActivity extends BaseActivity {


	public static final String SP_NAME_INIT = "sp_name_init";
	public static final String SP_KEY_IS_INIT = "sp_key_is_init";

	private LinearLayout view_six;
	private Button noCheckBox;
	private Button okCheckBox;
	private WebView mXieyiWeb;

	private ImageView[] imageViews;
	private ViewGroup group;
	private ViewPager pager;
	private ImageView imageView;
	private ArrayList<View> pageViews;
	// private Button button;

	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.init_2);
		LayoutInflater mInflater = (LayoutInflater) getSystemService(Service.LAYOUT_INFLATER_SERVICE);
		View xieyiView = mInflater.inflate(R.layout.init_xieyi, null);
		view_six = (LinearLayout) findViewById(R.id.init_xieyi_layout);
		noCheckBox = (Button) xieyiView.findViewById(R.id.init_no_cb);
		okCheckBox = (Button) xieyiView.findViewById(R.id.init_ok_cb);
		mXieyiWeb = (WebView) xieyiView.findViewById(R.id.init_xieyi_webview);
		mXieyiWeb.loadUrl("file:///android_asset/xieyi.html");
		
		group = (ViewGroup)findViewById(R.id.viewGroup);
		pager = (ViewPager)findViewById(R.id.guidePages);
		
		
		pageViews = new ArrayList<View>();
		pageViews.add(mInflater.inflate(R.layout.init_item_01, null));
		pageViews.add(mInflater.inflate(R.layout.init_item_02, null));
		pageViews.add(mInflater.inflate(R.layout.init_item_03, null));
		pageViews.add(mInflater.inflate(R.layout.init_item_04, null));
		pageViews.add(mInflater.inflate(R.layout.init_item_05, null));
		pageViews.add(xieyiView);
		
		imageViews = new ImageView[pageViews.size()];
		
		for(int i=0;i<pageViews.size();i++){
			imageView = new ImageView(getApplicationContext());
			imageView.setLayoutParams(new LayoutParams(20, 20));
			imageView.setPadding(20, 0, 20, 0);
			imageViews[i] = imageView;
			if(i==0){
				imageViews[i].setBackgroundResource(R.drawable.page_indicator_focused);
			}else{
				imageViews[i].setBackgroundResource(R.drawable.page_indicator);
			}
			group.addView(imageViews[i]);
		}
		
		pager.setAdapter(new GuidePageAdapter());
		pager.setOnPageChangeListener(new GuidePageChangeListener());
		
		
		noCheckBox.setOnClickListener(new Listener());
		okCheckBox.setOnClickListener(new Listener());
	}

	private boolean saveInited() {
		SharedPreferences preferences = getApplicationContext()
				.getSharedPreferences(InitActivity.SP_NAME_INIT,
						MODE_WORLD_READABLE);
		boolean isinit = preferences.getBoolean(InitActivity.SP_KEY_IS_INIT,
				false);
		if (!isinit) {
			Editor editor = preferences.edit();
			editor.putBoolean(InitActivity.SP_KEY_IS_INIT, true);
			editor.commit();
		}
		return isinit;
	}

	protected class Listener implements OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.init_no_cb:
				InitActivity.this.finish();
				break;
			case R.id.init_ok_cb:
				saveInited();
				Intent intent = new Intent();
				intent.setClass(InitActivity.this, MainActivityGroup.class);
				startActivity(intent);
				InitActivity.this.finish();
				break;
			default:
				break;
			}
		}
	}




	class GuidePageAdapter extends PagerAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return pageViews.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			// TODO Auto-generated method stub
			((ViewPager)container).removeView(pageViews.get(position));
		}

		@Override
		public int getItemPosition(Object object) {
			// TODO Auto-generated method stub
			return super.getItemPosition(object);
		}

		@Override
		public Object instantiateItem(View container, int position) {
			// TODO Auto-generated method stub
			((ViewPager)container).addView(pageViews.get(position));
			return pageViews.get(position);
		}
		
		
	}
	
	class GuidePageChangeListener implements OnPageChangeListener{

		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onPageSelected(int arg0) {
			// TODO Auto-generated method stub
			for(int i=0;i<imageViews.length;i++){
				imageViews[arg0].setBackgroundResource(R.drawable.page_indicator_focused);
				if(arg0!=i){
					imageViews[i].setBackgroundResource(R.drawable.page_indicator);
				}
			}
			if(arg0 == imageViews.length-1){
				group.setVisibility(View.GONE);
			}else{
				group.setVisibility(View.VISIBLE);
			}
		}
		
	}

}
