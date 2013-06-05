package com.icalinks.mobile.ui;

import java.util.List;

import android.app.ListActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.icalinks.common.NotifyHelper;
import com.icalinks.jocyjt.R;
import com.icalinks.mobile.GlobalApplication;
import com.icalinks.mobile.db.dal.MsgsDal;
import com.icalinks.mobile.db.dal.UserInfo;
import com.icalinks.mobile.ui.adapter.MsgsAdapter;
import com.icalinks.mobile.ui.model.MsgsItem;
import com.icalinks.mobile.util.ToastShow;
import com.markupartist.android.widget.ActionBar;

public class MsgsRecordActivity extends ListActivity implements
		View.OnClickListener, TextWatcher {
	public void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.msgs_record);
		initView();
		mPageNumber = 1;
		mTxtPageNumber.setText("" + mPageNumber);
		loadRecordCount();
		loadList();
	}

	private static final int PAGE_SIZE = 10;
	private int mPageNumber;
	private int mPageCount;
	private List<MsgsItem> mMsgsItemList;
	private MsgsAdapter mMsgsAdapter;
	private ActionBar mActionBar;
	private Button mViewback;

	private Button mBtnNextPage;
	private Button mBtnPrevPage;
	private Button mBtnDelAll;
	private TextView mLblPageCount;
	private EditText mTxtPageNumber;

	private void initView() {
		mActionBar = (ActionBar) findViewById(R.id.msgs_record_actionbar);
		mActionBar.setTitle(R.string.msgs_record_title);
		mViewback = mActionBar.showBackButton();
		mViewback.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				MsgsRecordActivity.this.finish();
			}
		});

		mLblPageCount = (TextView) findViewById(R.id.msgs_record_count);
		mTxtPageNumber = (EditText) findViewById(R.id.msgs_record_pagenumber);
		mBtnPrevPage = (Button) findViewById(R.id.msgs_record_prev);
		mBtnNextPage = (Button) findViewById(R.id.msgs_record_next);
		mBtnDelAll = (Button) findViewById(R.id.msgs_record_delall);

		mBtnPrevPage.setOnClickListener(this);
		mBtnNextPage.setOnClickListener(this);
		mBtnDelAll.setOnClickListener(this);
		mTxtPageNumber.addTextChangedListener(this);
	}

	private void loadRecordCount() {
		UserInfo user = GlobalApplication.getApplication().getCurrUser();
		if (user != null) {
			// 获取总记录数/计算总页数、设置总页数
			int count = MsgsDal.getInstance(this).select(user.getVid());
			mPageCount = count / PAGE_SIZE + (count % PAGE_SIZE != 0 ? 1 : 0);
			mLblPageCount.setText("/" + mPageCount);

			// 判断安全的页面范围,设置页码显示

			// mTxtPageNumber.setText("" + mPageNumber);
		} else {
			ToastShow.show(this, R.string.toast_not_logged_operation_alert);
		}
	}

	private void loadList() {
		UserInfo user = GlobalApplication.getApplication().getCurrUser();
		if (user != null) {
			// 更新页面数据
			mMsgsItemList = MsgsDal.getInstance(this).select(user.getVid(),
					PAGE_SIZE, mPageNumber);
			mMsgsAdapter = new MsgsAdapter(this);
			mMsgsAdapter.setItemList(mMsgsItemList);
			setListAdapter(mMsgsAdapter);
		} else {
			ToastShow.show(this, R.string.toast_not_logged_operation_alert);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.msgs_record_prev:
			mPageNumber--;
			if (mPageNumber < 1) {
				mPageNumber = 1;
			}
			mTxtPageNumber.setText("" + mPageNumber);
			// loadList();
			break;
		case R.id.msgs_record_next:
			mPageNumber++;
			loadRecordCount();
			if (mPageNumber > mPageCount) {
				mPageNumber = mPageCount;
			}
			mTxtPageNumber.setText("" + mPageNumber);
			// loadList();
			break;
		case R.id.msgs_record_delall:
			break;
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub

	}

//	private int mPrevPageNum;

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		String strPnum = "" + mTxtPageNumber.getText();
		if (strPnum != null && !strPnum.trim().equals("")) {
			try {
				mPageNumber = Integer.parseInt(strPnum);
				// if (mPageNumber != mPrevPageNum) {
				loadRecordCount();
				if (mPageNumber > mPageCount) {
					mPageNumber = mPageCount;
				}
				loadList();
//				mPrevPageNum = mPageNumber;
				// }
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub

	}
}
