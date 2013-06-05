package com.icalinks.mobile.ui;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.icalinks.jocyjt.R;
import com.icalinks.mobile.GlobalApplication;
import com.icalinks.mobile.db.dal.UserDal;
import com.icalinks.mobile.db.dal.UserInfo;
import com.icalinks.mobile.ui.activity.AbsSubActivity;
import com.icalinks.mobile.ui.adapter.MoreActMgrAdapter;
import com.icalinks.mobile.util.ToastShow;
import com.icalinks.mobile.widget.PopupWindowDialog;
import com.icalinks.obd.vo.VehicleInfo;
import com.provider.model.Result;
import com.provider.model.resources.OBDHelper;
import com.provider.net.listener.OnCallbackListener;
/**
 * 车辆账户管理
 * @author Administrator
 *
 */
public class MoreActMgrActivity extends AbsSubActivity implements
		AdapterView.OnItemClickListener, OnCallbackListener, OnClickListener {
	private String TAG = MoreActMgrActivity.class.getName();

	private Button btn_edit;
	private Button btn_add;
	private ListView lsv_act;
	private PopupWindowDialog mPppChooseDialog;
	
	
	private LinearLayout userInfoLayout;

	private List<UserInfo> mUserList;
	public static final String ARGS_NICENAME = "args_nickname";
	public static final String ARGS_USERNAME = "args_username";
	public static final String ARGS_PASSWORD = "args_password";

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.more_actmgr);
		if (mPppChooseDialog != null) {
			mPppChooseDialog.destroy();
			mPppChooseDialog = null;
		}
	}


	private void initView() {
		mMoreActMgrAdapter = new MoreActMgrAdapter(this);
		userInfoLayout = (LinearLayout)findViewById(R.id.more_actmgr_layout);
		
		btn_edit = showRightButton("编辑");
		btn_edit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				loadUserList(!mMoreActMgrAdapter.isEdit());
			}
		});
		btn_add = (Button) findViewById(R.id.more_actmgr_add_btn);
		btn_add.setOnClickListener(this);

		lsv_act = (ListView) findViewById(R.id.more_actmgr_listview);
	
		
		
		lsv_act.setAdapter(mMoreActMgrAdapter);
		
		showBackButton().setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				goback();
			}
		});
	}

	// private boolean mEditButtonStatus;
	private static final int REQ_CODE_FAST = 0x1231;

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			loadUserList(false);
			// 扫描二维码返回
			if (REQ_CODE_ADD_ACCOUNT_UPDATA == requestCode) {
				// 登录第一个账号
				UserInfo info = mUserList.get(0);
				OBDHelper.validUser(info.name, info.pswd,
						MoreActMgrActivity.this);
			}
		} else if (resultCode == RESULT_CANCELED) {
		}
	}

	public static final int REQ_CODE_ADD_ACCOUNT = 0x0001;
	public static final int REQ_CODE_ADD_ACCOUNT_UPDATA = 0x0002;
	public static final int REQ_CODE_ADD_ACCOUNT_UPDATA_NAME = 0x0003;
	public static final int REQ_CODE_ADD_ACCOUNT_UPDATA_PASSWORD = 0x0004;
	public static final int REQ_CODE_ADD_ACCOUNT_UPDATA_PASSWORD_WRONG = 0x0005;

	private void loadUserList(boolean isEdit) {
		mMoreActMgrAdapter.setEdit(isEdit);
//从服务器获取用户li
		mUserList = UserDal.getInstance(this).select(null);
		//添加一个默认用户
		UserInfo user = new UserInfo(GlobalApplication.getApplication().getUserName(), "123456");
		mUserList.add(user);
		if (mUserList.size() > 0) {
			mUserList.get(0)._id = -1;
			userInfoLayout.setVisibility(View.VISIBLE);
		}else{
			userInfoLayout.setVisibility(View.INVISIBLE);
		}
		mMoreActMgrAdapter.setItemList(mUserList);
		lsv_act.setAdapter(mMoreActMgrAdapter);
		lsv_act.setOnItemClickListener(this);
	}

	private MoreActMgrAdapter mMoreActMgrAdapter;

	public void onFailure(Result arg0) {
		hideLoginDialog();
		Toast.makeText(this, "" + arg0.head.resMsg, 0).show();
		GlobalApplication.getApplication().getObd().stopReadOBD();
	}

	public void onSuccess(Result arg0) {
		
		Toast.makeText(this, "登录成功", 0).show();

		hideLoginDialog();

		VehicleInfo vehicleInfo = GlobalApplication.getVehicleInfo();
		if (vehicleInfo != null) {
			UserDal.getInstance(this).update(vehicleInfo.getLicensePlate(),
					vehicleInfo.getPassword(), vehicleInfo.getLoginName(),
					vehicleInfo.getVid());

			mUserList = UserDal.getInstance(this).select(null);
			if (mUserList.size() > 0) {
				mUserList.get(0)._id = -1;
			}
			mMoreActMgrAdapter.setItemList(mUserList);
			lsv_act.setAdapter(mMoreActMgrAdapter);
			lsv_act.setOnItemClickListener(this);
			// // 更新当前用户
			// GlobalApplication.getApplication().setCurrUser(userinfo);
			setResult(RESULT_OK);
			ToastShow.show(this, "登陆成功");
			Log.e(TAG, "登陆成功");
			
			GlobalApplication.getApplication().startObd();
		} else {
			Toast.makeText(MoreActMgrActivity.this, "登录失败", Toast.LENGTH_SHORT)
					.show();
			GlobalApplication.getApplication().stopObd();
		}
		goback();
	}

	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// mCurrListIndex = arg2;

		showLoginDialog();
		OBDHelper.validUser(mUserList.get(arg2).name, mUserList.get(arg2).pswd,
				MoreActMgrActivity.this);

	}

	// private AlertDialog mLoginDialog;;

	private void showLoginDialog() {
	}

	private void hideLoginDialog() {
	}

	@Override
	protected void onResume() {
		super.onResume();
		setTitle(getString(R.string.gd_clzhgl));
		initView();
		loadUserList(false);
		if (mPppChooseDialog != null) {
			mPppChooseDialog.destroy();
			mPppChooseDialog = null;
		}

	}

	@Override
	public void onClick(View v) {
		if (checkPhoneNumber()) {
			if (v == btn_add) {
				Intent intent = new Intent();
				intent.setClass(MoreActMgrActivity.this,
						MoreLoginActivity.class);
				startActivityForResult(intent, REQ_CODE_ADD_ACCOUNT);
			}
			// if (v == twoScanLinearLayout) {
			// Intent intent = new Intent();
			// intent.setClass(MoreActMgrActivity.this, CaptureActivity.class);
			// startActivityForResult(intent, REQ_CODE_ADD_ACCOUNT_UPDATA);
			//
			// }
		} else {
			ToastShow.show(this, "请先设置手机号码，再进行相关操作！");
			// 转到设置手机号码页面
			Intent intent = new Intent();
			intent.setClass(this, MoreTelephoneActivity.class);
			this.startActivity(intent);
		}
	}

	private boolean checkPhoneNumber() {
		String strPhoneNumber = GlobalApplication.getPhoneNumberFromShare(this);
		if (strPhoneNumber == null || strPhoneNumber.trim().equals("")) {
			return false;
		}
		return true;
	}

	public void delete(int position) {
		UserInfo user = (UserInfo) mMoreActMgrAdapter.getItem(position);
		try {
			UserDal.getInstance(this).delete(Integer.valueOf(user.vid));
			GlobalApplication.getApplication().setCurrUser(null);
			Toast.makeText(this, "该用户已删除", 1000).show();
			loadUserList(false);
		} catch (Exception ex) {
		}
	}

	public void modify(int position) {
		UserInfo user = (UserInfo) mMoreActMgrAdapter.getItem(position);
		showChooseDialog(user);
	}

	Button more_choose_password_btn;
	Button more_choose_account_btn;

	private void showChooseDialog(final UserInfo info) {

		if (mPppChooseDialog == null) {
			mPppChooseDialog = new PopupWindowDialog(MoreActMgrActivity.this);

			mPppChooseDialog.setContentView(R.layout.dialog_more_choose);
			View chooseView = mPppChooseDialog.getContentView();
			Button more_choose_password_btn = (Button) chooseView
					.findViewById(R.id.more_choose_password_btn);
			more_choose_account_btn = (Button) chooseView
					.findViewById(R.id.more_choose_account_btn);
			more_choose_password_btn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					intent.setClass(MoreActMgrActivity.this,
							MoreReActPwdActivity.class);
					intent.putExtra(ARGS_USERNAME, info.name);

					intent.putExtra(ARGS_PASSWORD, info.pswd);
					startActivityForResult(intent,
							REQ_CODE_ADD_ACCOUNT_UPDATA_PASSWORD);
				}
			});
			more_choose_account_btn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					intent.setClass(MoreActMgrActivity.this,
							MoreActMdfActivity.class);

					intent.putExtra(ARGS_USERNAME, info.name);
					intent.putExtra(ARGS_PASSWORD, info.pswd);
					intent.putExtra(ARGS_NICENAME, info.nick);

					startActivityForResult(intent,
							REQ_CODE_ADD_ACCOUNT_UPDATA_NAME);

				}
			});
		}
		mPppChooseDialog.showFullScreen();
	}
}
