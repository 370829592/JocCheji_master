package com.icalinks.mobile.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;

import com.icalinks.jocyjt.R;
import com.icalinks.mobile.recver.ActionBarHelper;
import com.icalinks.mobile.ui.adapter.ViewPagerAdapter;
import com.icalinks.mobile.ui.fragment.MoreCenterFragment;

public class MoreActivity extends BaseActivity implements
        ViewPager.OnPageChangeListener {

    private ViewPager mViewPager;
    private int mViewPagerPosition;
    private ViewPagerAdapter mViewPagerAdapter;
    private List<View> mViewList;

    private MoreCenterFragment mMoreCenterFragment;

    // private MoreLinkedFragment mMoreLinkedFragment;
    // private MoreModifyFragment mMoreModifyFragment;
    // private MoreOnekeyFragment mMoreOnekeyFragment;

    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        if (mViewPager == null) {
            mViewPager = new ViewPager(this);
            mViewPager.setOnPageChangeListener(this);
            mViewPagerAdapter = new ViewPagerAdapter();

            // View pager
            mMoreCenterFragment = new MoreCenterFragment(R.string.home_btn_more);
            mMoreCenterFragment.setActivity(this);
            LayoutInflater inflater = (LayoutInflater) getSystemService(Service.LAYOUT_INFLATER_SERVICE);
            mViewList = new ArrayList<View>();
            mViewList.add(mMoreCenterFragment.onCreateView(inflater,
                    mViewPager, savedInstanceState));

            mViewPagerAdapter.setViews(mViewList);
            mViewPager.setAdapter(mViewPagerAdapter);
        }
        setContentView(mViewPager);
    }

    protected void onResume() {
        super.onResume();
        ActionBarHelper.setTitle("更多");
    }

    public void onPageScrollStateChanged(int arg0) {
        // TODO Auto-generated method stub

    }

    public void onPageScrolled(int arg0, float arg1, int arg2) {
        // TODO Auto-generated method stub

    }

    public void onPageSelected(int arg0) {
        // TODO Auto-generated method stub

    }

}
