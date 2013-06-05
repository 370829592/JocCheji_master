package com.icalinks.mobile.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.MapPointInfo;
import com.icalinks.jocyjt.R;
import com.icalinks.mobile.GlobalApplication;
import com.icalinks.mobile.db.dal.UserInfo;
import com.icalinks.mobile.recver.ActionBarHelper;
import com.icalinks.mobile.ui.adapter.ViewPagerAdapter;
import com.icalinks.mobile.ui.fragment.BaseFragment;
import com.icalinks.mobile.ui.fragment.InfoInsureFragment;
import com.icalinks.mobile.ui.fragment.RmctCarinfFragment;
import com.icalinks.mobile.ui.fragment.RmctCenterFragment;
import com.umeng.analytics.MobclickAgent;
/**
 * 汽车卫士
 * @author Administrator
 *
 */
//public class RmctActivity extends MapActivity implements
public class RmctActivity extends BaseActivity implements
		ViewPager.OnPageChangeListener {

	private ViewPager mViewPager;
	private ViewGroup mViewPaperSin;
	private int mViewPagerPosition;
	private ViewPagerAdapter mViewPagerAdapter;
	private List<View> mViewList;
	private List<BaseFragment> mFragmentList;

	private ImageView imageView;
	private ImageView[] imageViews;
	private int viewPagerCount = 2;

	private RmctCarinfFragment mRmctCarinfFragment;
//	private RmctCenterFragment mRmctCenterFragment;
	// private RmctRoutesFragment mRmctRoutesFragment;
	private InfoInsureFragment mInfoInsureFragment;

	// private RmctRecordFragment mRmctRecordFragment;

	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.rmct);
		if (mViewPager == null) {
			// setContentView(mViewPager);
			// mViewPager = new ViewPager(this);

			mViewPager = (ViewPager) findViewById(R.id.rmct_viewpager);
			mViewPaperSin = (ViewGroup) findViewById(R.id.rmct_indicator);
			mViewPager.setOnPageChangeListener(this);

			mFragmentList = new ArrayList<BaseFragment>();
			mViewList = new ArrayList<View>();
			LayoutInflater inflater = (LayoutInflater) getSystemService(Service.LAYOUT_INFLATER_SERVICE);
			{
				mFragmentList.add(mRmctCarinfFragment = new RmctCarinfFragment(
						R.string.rmct_carinf_title));
				mRmctCarinfFragment.setActivity(this);
				mViewList.add(mRmctCarinfFragment.onCreateView(inflater,
						mViewPager, bundle));

				// mFragmentList.add(mRmctRoutesFragment = new
				// RmctRoutesFragment(
				// R.string.rmct_routes_title));
				// mRmctRoutesFragment.setActivity(this);
				// mViewList.add(mRmctRoutesFragment.onCreateView(inflater,
				// mViewPager, bundle));

//				mFragmentList.add(mRmctCenterFragment = new RmctCenterFragment(
//						R.string.rmct_center_title));
//				mRmctCenterFragment.setActivity(this);
//				mViewList.add(mRmctCenterFragment.onCreateView(inflater,
//						mViewPager, bundle));

				mFragmentList.add(mInfoInsureFragment = new InfoInsureFragment(
						R.string.info_insure));
				mInfoInsureFragment.setActivity(this);
				mViewList.add(mInfoInsureFragment.onCreateView(inflater,
						mViewPager, bundle));
			}

			mViewPagerAdapter = new ViewPagerAdapter();
			mViewPagerAdapter.setViews(mViewList);
			mViewPager.setAdapter(mViewPagerAdapter);

			imageViews = new ImageView[viewPagerCount];
			for (int i = 0; i < imageViews.length; i++) {
				imageView = new ImageView(this);
				imageView.setLayoutParams(new LayoutParams(20, 20));
				imageView.setPadding(20, 0, 20, 0);
				imageViews[i] = imageView;
				if (0 == i) {
					imageViews[i].setBackgroundResource(R.drawable.pager_dot_p);
				} else {
					imageViews[i].setBackgroundResource(R.drawable.pager_dot_n);
				}
				mViewPaperSin.addView(imageView);
			}

//			mApplication = GlobalApplication.getApplication();
//			mApplication.onCreateMapManager();
//			mApplication.onCreateMapActivity(this);
		}
	}
 
	private UserInfo mProvUserInfo;
	protected UserInfo mCurrUserInfo;

	public void reload() {

		Intent intent = getIntent();
		overridePendingTransition(0, 0);
		intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		finish();

		overridePendingTransition(0, 0);
		startActivity(intent);
	}

	protected void onResume() {
//		mApplication.onResumeMapActivity();
		super.onResume();
		mCurrUserInfo = GlobalApplication.getApplication().getCurrUser();
		if (mProvUserInfo != null) {
			if (!mProvUserInfo.equals(mCurrUserInfo)) {
				// reload();
			}
		} else {
			mProvUserInfo = mCurrUserInfo;
		}
		ActionBarHelper.setTitle(mFragmentList.get(mViewPagerPosition)
				.getTitle());
		mFragmentList.get(mViewPagerPosition).onResume();

		MobclickAgent.onResume(this);
	}

	protected void onPause() {
//		mApplication.onPauseMapActivity();
		super.onPause();
		mFragmentList.get(mViewPagerPosition).onPause();

		MobclickAgent.onPause(this);
	}
//	protected boolean isRouteDisplayed() {
//		return false;
//	}

	public void onPageScrollStateChanged(int arg0) {
		mViewPaperSin.setVisibility(View.GONE);
	}

	public void onPageScrolled(int arg0, float arg1, int arg2) {
		if (mViewPaperSin.getVisibility() == View.GONE) {
			mViewPaperSin.setVisibility(View.VISIBLE);
		}
	}

	public void onPageSelected(int position) {
		mFragmentList.get(mViewPagerPosition).onPause();

		mFragmentList.get(position).onResume();
		ActionBarHelper.setTitle(mFragmentList.get(
				mViewPagerPosition = position).getTitle());

		for (int i = 0; i < imageViews.length; i++) {
			imageViews[position].setBackgroundResource(R.drawable.pager_dot_p);
			if (position != i) {
				imageViews[i].setBackgroundResource(R.drawable.pager_dot_n);
			}
		}
	}

	public void setPageMap() {

	}
}
