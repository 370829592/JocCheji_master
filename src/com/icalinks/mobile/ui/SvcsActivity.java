package com.icalinks.mobile.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

import com.baidu.mapapi.MapActivity;
import com.icalinks.jocyjt.R;
import com.icalinks.mobile.recver.ActionBarHelper;
import com.icalinks.mobile.ui.adapter.ViewPagerAdapter;
import com.icalinks.mobile.ui.fragment.BaseFragment;
import com.icalinks.mobile.ui.fragment.SvcsCenterFragment;
import com.icalinks.mobile.ui.fragment.SvcsRecordFragment;
import com.provider.common.JocApplication;
import com.provider.net.tcp.GpsWorker;
import com.umeng.analytics.MobclickAgent;
/**
 * 汽车秘书
 * @author Administrator
 *
 */

public class SvcsActivity extends MapActivity implements
		ViewPager.OnPageChangeListener {

	private ViewPager mViewPager;
	private int mViewPagerPosition;
	private ViewPagerAdapter mViewPagerAdapter;
	private List<BaseFragment> mFragmentList;
	private List<View> mViewList;
	private ImageView[] mImageViewsArr;
	private ViewGroup mViewPaperSin;
	private static Handler sHandler = new Handler(Looper.getMainLooper());

	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.svcs);

		JocApplication.syncPhoneNumber(this);
		System.out.println("SvcsActivity => onCreate(.)...");

		mViewPager = (ViewPager) findViewById(R.id.svcs_viewpager);
		mViewPaperSin = (ViewGroup) findViewById(R.id.svcs_sinpaper);

		mViewPager.setOnPageChangeListener(this);
		mViewPagerAdapter = new ViewPagerAdapter();

		if (mFragmentList == null) {
			mViewList = new ArrayList<View>();
			mFragmentList = new ArrayList<BaseFragment>();
			LayoutInflater inflater = (LayoutInflater) getSystemService(Service.LAYOUT_INFLATER_SERVICE);
			// page 1
			SvcsCenterFragment centerFragment = new SvcsCenterFragment(this,
					R.string.svcs_center_title);
			centerFragment.setPageIndex(0);
			mFragmentList.add(centerFragment);
			mViewList.add(centerFragment.onCreateView(inflater, mViewPager,
					bundle));
			// page 2
			// centerFragment = new SvcsCenterFragment(this,
			// R.string.svcs_center_title);
			// centerFragment.setPageIndex(1);
			// mFragmentList.add(centerFragment);
			// mViewList.add(centerFragment.onCreateView(inflater, mViewPager,
			// bundle));
			// page 3
			SvcsRecordFragment recordFragment = new SvcsRecordFragment(this,
					R.string.svcs_record_title);
			recordFragment = new SvcsRecordFragment(this,
					R.string.svcs_record_title);

			mFragmentList.add(recordFragment);
			mViewList.add(recordFragment.onCreateView(inflater, mViewPager,
					bundle));

			mViewPagerAdapter.setViews(mViewList);
			mViewPager.setAdapter(mViewPagerAdapter);
		}

		initSinpaperData();

	}

	public void toNavi(final View v) {
		Intent intent = new Intent();
		intent.setClass(this, NaviActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

		startActivity(intent);
		testData(v.getContext());
	}

	public void testData(final Context context) {
		sHandler.postDelayed(new Runnable() {

			public void run() {
				final String sRouteDataDst = "true,113.97716,22.59754,科技园;true,113.579500,22.357000,清华大学深圳研究生院";
				GpsWorker gpsWorker = new GpsWorker();
				gpsWorker.sendGPSRoutePlanBMap(context, sRouteDataDst);

				// String data =
				// "292988002B1DE083373131342E32333435362C32322E32333435362CC7E5BBAAB4F3D1A7C9EEDBDAD1D0BEBFD4BA940D";
				// byte[] revData = ByteCodec.hexStr2Bytes_(data);
				// byte[] testData = new byte[revData.length * 2];
				// System.arraycopy(revData, 0, testData, 0, revData.length);
				// System.arraycopy(revData, 0, testData, revData.length,
				// revData.length);

				// DispachManager dispachManager = new DispachManager(null);
				// dispachManager.handleMessageGPS(revData);
			}
		}, 5 * 1000);
	}

	private void initSinpaperData() {
		mImageViewsArr = new ImageView[mFragmentList.size()];
		for (int i = 0; i < mImageViewsArr.length; i++) {
			ImageView imageView = new ImageView(this);
			imageView.setLayoutParams(new LayoutParams(20, 20));
			imageView.setPadding(20, 0, 20, 0);
			mImageViewsArr[i] = imageView;
			if (0 == i) {
				mImageViewsArr[i].setBackgroundResource(R.drawable.pager_dot_p);
			} else {
				mImageViewsArr[i].setBackgroundResource(R.drawable.pager_dot_n);
			}
			mViewPaperSin.addView(imageView);
		}
		sHandler.postDelayed(new Runnable() {

			public void run() {
				mViewPaperSin.setVisibility(View.GONE);
			}
		}, 3 * 1000);
	}

	protected void onResume() {
		super.onResume();
		mFragmentList.get(mViewPagerPosition).onResume();
		ActionBarHelper.setTitle(mFragmentList.get(mViewPagerPosition)
				.getTitle());
		MobclickAgent.onResume(this);
	}

	protected void onPause() {
		super.onPause();
		mFragmentList.get(mViewPagerPosition).onPause();
		MobclickAgent.onPause(this);
	}

	protected void onStop() {
		super.onStop();
	}

	protected void onDestroy() {
		super.onDestroy();
	}

	protected boolean isRouteDisplayed() {
		return false;
	}

	public void onPageScrollStateChanged(int arg0) {
		// System.out.println("onPageScrollStateChanged..............");
		mViewPaperSin.setVisibility(View.GONE);
	}

	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// System.out.println("onPageScrolled..............");
		if (mViewPaperSin.getVisibility() == View.GONE) {
			mViewPaperSin.setVisibility(View.VISIBLE);
		}
	}

	public void onPageSelected(int position) {
		// System.out.println("onPageSelected..............");
		mViewPaperSin.setVisibility(View.GONE);
		mFragmentList.get(mViewPagerPosition).onPause();
		mFragmentList.get(position).onResume();
		updateSinpaper(position);
		ActionBarHelper.setTitle(mFragmentList.get(
				mViewPagerPosition = position).getTitle());
	}

	private void updateSinpaper(int papeIndex) {
		for (int i = 0; i < mImageViewsArr.length; i++) {
			mImageViewsArr[papeIndex]
					.setBackgroundResource(R.drawable.pager_dot_p);
			if (papeIndex != i) {
				mImageViewsArr[i].setBackgroundResource(R.drawable.pager_dot_n);
			}
		}
	}

//	public void showRecordFragment(int optionId) {
//		mShowRecordOption = optionId;
//		mViewPager.setCurrentItem(1);
//	}

//	private int mShowRecordOption = 0;
//
//	public int getShowRecordOption() {
//		return mShowRecordOption;
//	}
//	public void clearShowRecordOption(){
//		mShowRecordOption=0;
//	}
}
