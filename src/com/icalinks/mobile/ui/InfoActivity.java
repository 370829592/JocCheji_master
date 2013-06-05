package com.icalinks.mobile.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

import com.icalinks.jocyjt.R;
import com.icalinks.mobile.recver.ActionBarHelper;
import com.icalinks.mobile.ui.adapter.ViewPagerAdapter;
import com.icalinks.mobile.ui.fragment.BaseFragment;
import com.icalinks.mobile.ui.fragment.InfoRecordFragment;
import com.icalinks.mobile.ui.fragment.InfoRtdataFragment;
import com.icalinks.mobile.ui.fragment.InfoSingleFragment;
import com.icalinks.mobile.ui.fragment.InfoSsdataFragment;
import com.icalinks.mobile.ui.fragment.InfoStatusFragment;

/**
 * 车管家
 * 
 * @author zg_hu@icalinks.com.cn
 * 
 * 
 */
// InstrumentedActivity
public class InfoActivity extends BaseActivity implements
		ViewPager.OnPageChangeListener {

	public static final String TAG = InfoActivity.class.getSimpleName();

	private ViewPager mViewPager;
	private ViewGroup pagerDot;
	private int mViewPagerPosition;
	private ViewPagerAdapter mViewPagerAdapter;
	private List<View> mViewList;
	private List<BaseFragment> mFragmentList;

	private ImageView imageView;
	private ImageView[] imageViews;
	private int viewPagerCount = 5;

	public String simCard;

	private InfoRtdataFragment mInfoRtdataFragment;
	private InfoSsdataFragment mInfoSsdataFragment;
	private InfoSingleFragment mInfoSingleFragment;
	private InfoStatusFragment mInfoStatusFragment;
	private InfoRecordFragment mInfoRecordFragment;
//	private InfoInsureFragment mInfoInsureFragment;

	private Thread postThread;
	private boolean potFlag = false;

	private static final int POT_GONE = 0x00;

	private Handler mHandler = new Handler() {

		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case POT_GONE:
				pagerDot.setVisibility(View.GONE);
				potFlag = false;
				break;
			}
		}

	};

	protected void onCreate(Bundle savedInstanceState) {
		// Log.e(TAG, "onCreate");
		super.onCreate(savedInstanceState);

		setContentView(R.layout.info);

		mViewPager = (ViewPager) findViewById(R.id.info_viewpager);
		pagerDot = (ViewGroup) findViewById(R.id.info_indicator);

		if (mViewList == null) {
			mViewPagerAdapter = new ViewPagerAdapter();
			mViewList = new ArrayList<View>();
			LayoutInflater inflater = (LayoutInflater) getSystemService(Service.LAYOUT_INFLATER_SERVICE);

			mFragmentList = new ArrayList<BaseFragment>();
			{
				mFragmentList.add(mInfoRtdataFragment = new InfoRtdataFragment(
						R.string.info_rtdata));
				mInfoRtdataFragment.setActivity(this);
				mViewList.add(mInfoRtdataFragment.onCreateView(inflater,
						mViewPager, savedInstanceState));

				mFragmentList.add(mInfoSsdataFragment = new InfoSsdataFragment(
						R.string.info_ssdata));
				mInfoSsdataFragment.setActivity(this);
				mViewList.add(mInfoSsdataFragment.onCreateView(inflater,
						mViewPager, savedInstanceState));

				mFragmentList.add(mInfoSingleFragment = new InfoSingleFragment(
						R.string.info_single));
				mInfoSingleFragment.setActivity(this);
				mViewList.add(mInfoSingleFragment.onCreateView(inflater,
						mViewPager, savedInstanceState));
				
				mFragmentList.add(mInfoStatusFragment = new InfoStatusFragment(
						R.string.info_status));
				mInfoStatusFragment.setActivity(this);
				mViewList.add(mInfoStatusFragment.onCreateView(inflater,
						mViewPager, savedInstanceState));

				// begin
				mFragmentList.add(mInfoRecordFragment = new InfoRecordFragment(
						R.string.info_record));
				mInfoRecordFragment.setActivity(this);
				mViewList.add(mInfoRecordFragment.onCreateView(inflater,
						mViewPager, savedInstanceState));
				// end

//				mFragmentList.add(mInfoInsureFragment = new InfoInsureFragment(
//						R.string.info_insure));
//				mInfoInsureFragment.setActivity(this);
//				mViewList.add(mInfoInsureFragment.onCreateView(inflater,
//						mViewPager, savedInstanceState));

			}
			mViewPagerAdapter.setViews(mViewList);
			mViewPager.setAdapter(mViewPagerAdapter);
			// mViewPager.setCurrentItem(0);
		}

		// view pager dots
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
			pagerDot.addView(imageView);
		}

		mViewPager.setOnPageChangeListener(this);

		postThread = new Thread(new PotRunnable());
		potFlag = true;
		postThread.start();

	}
	
//	//显示编辑按钮
//	public void onFresh(boolean isShowBtn) {
//		mInfoRecordFragment.onFresh(isShowBtn);
//	}
//	
	protected void onResume() {
		// TODO Auto-generated method stub
		// Log.e(TAG, "onResume");
		super.onResume();
		mFragmentList.get(mViewPagerPosition).onResume();
		ActionBarHelper.setTitle(mFragmentList.get(mViewPagerPosition)
				.getTitle());
	}

	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mFragmentList.get(mViewPagerPosition).onPause();
	}

	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub
		pagerDot.setVisibility(View.GONE);
	}

	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
		if (pagerDot.getVisibility() == View.GONE) {
			pagerDot.setVisibility(View.VISIBLE);
		}
	}

	public void onPageSelected(int arg0) {
		// ActionBarHelper.setTitle(mFragmentList.get(mViewPagerPosition =
		// arg0).getTitle());
		// if (pagerDot.getVisibility() == View.GONE) {
		// pagerDot.setVisibility(View.VISIBLE);
		// }

		mFragmentList.get(mViewPagerPosition).onPause();
		mFragmentList.get(arg0).onResume();
		ActionBarHelper.setTitle(mFragmentList.get(mViewPagerPosition = arg0)
				.getTitle());

		for (int i = 0; i < imageViews.length; i++) {
			imageViews[arg0].setBackgroundResource(R.drawable.pager_dot_p);
			if (arg0 != i) {
				imageViews[i].setBackgroundResource(R.drawable.pager_dot_n);
			}
		}

	}

	/**
	 * 获取touch事件
	 */

	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub

		return super.onTouchEvent(event);
	}

	/**
	 * 导航点消失
	 * 
	 * @author Administrator
	 * 
	 */
	class PotRunnable implements Runnable {

		public void run() {
			// TODO Auto-generated method stub
			while (potFlag) {
				try {
					Thread.sleep(3000);
					potInfo();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	};

	private void potInfo() {
		Message msg = new Message();
		msg.what = POT_GONE;
		mHandler.sendMessage(msg);
	}

}
