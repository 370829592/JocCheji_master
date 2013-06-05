package com.icalinks.mobile.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

public class ViewPagerAdapter extends PagerAdapter {
	private List<View> mListViews;

	// public ViewPagerAdapter(ArrayList<View> views) {
	// setViews( views)
	// }

	public void setViews(List<View> views) {
		mListViews = views;
		if (mListViews == null) {
			mListViews = new ArrayList<View>();
		}
	}

	@Override
	public void destroyItem(View collection, int position, Object arg2) {
		((ViewPager) collection).removeView(mListViews.get(position));
	}

	@Override
	public int getCount() {
		return mListViews.size();
	}

	@Override
	public Object instantiateItem(View collection, int position) {
		((ViewPager) collection).addView(mListViews.get(position), 0);
		return mListViews.get(position);
	}

	@Override
	public boolean isViewFromObject(View view, Object obj) {
		return view == (obj);
	}

	@Override
	public void finishUpdate(View arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public Parcelable saveState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void startUpdate(View arg0) {
		// TODO Auto-generated method stub

	}

}
