//package com.icalinks.mobile.ui;
//
//import java.io.BufferedReader;
//import java.io.FileInputStream;
//import java.io.InputStreamReader;
//
//import com.icalinks.jocyjt.R;
//import com.icalinks.mobile.GlobalApplication;
//import com.icalinks.mobile.db.dal.UserInfo;
//import com.markupartist.android.widget.ActionBar;
//import com.provider.model.Result;
//import com.provider.model.resources.OBDHelper;
//import com.provider.net.listener.OnCallbackListener;
//
//import android.app.Activity;
//import android.content.SharedPreferences;
//import android.content.SharedPreferences.Editor;
//import android.os.Bundle;
//import android.view.KeyEvent;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//public class MoreLoginPassworldManager extends Activity {
//	private Button back;
//	private Button save;
//	private EditText oldePassworld;
//	private EditText newpassworld;
//	private EditText copypassworld;
//
//	private LinearLayout oldPasswordLinearLayout;
//	private SharedPreferences mSharedPreferences;
//	private String mOldLoginPassword;
//	private String userName;
//	private UserInfo mUserInfo = new UserInfo();
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		// TODO Auto-generated method stub
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.more_car_login_passwordcon);
//		showView();
//
//		mOldLoginPassword = mUserInfo.pswd;
//		if (mOldLoginPassword != null) {
//			oldePassworld.setVisibility(View.VISIBLE);
//
//			oldePassworld.setFocusable(true);
//		} else {
//			newpassworld.setFocusable(true);
//		}
//
//		GlobalApplication.getApplication().setCurrUser(null);
//
//		mUserInfo.name = (String) getIntent().getExtras().get(
//				MoreAccountManageActivity.ARGS_USER_NAME);
//		mUserInfo.pswd = (String) getIntent().getExtras().get(
//				MoreAccountManageActivity.ARGS_USER_PASSWORLD);
//		System.out.println("userInfo.pswd:" + mUserInfo.pswd);
//	}
//
//	private ActionBar mActionBar;
//
//	private void showView() {
//		mActionBar = (ActionBar) findViewById(R.id.more_account_login_actionbar);
//		mActionBar.setTitle(R.string.more_center_rmctpswd_title);
//		back = mActionBar.showBackButton();
//		back.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				MoreLoginPassworldManager.this.finish();
//			}
//		});
//
//		save = mActionBar.showButton("保存");
//		save.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				save();
//			}
//		});
//
//		// back = (Button)
//		// findViewById(R.id.more_login_car_passwordcon_back_btn);
//		// save = (Button) findViewById(R.id.more_login_car_password_save_btn);
//		// save.setOnClickListener(new Linsener());
//		// back.setOnClickListener(new Linsener());
//
//		oldePassworld = (EditText) findViewById(R.id.more_login_originalpassword_et);
//		newpassworld = (EditText) findViewById(R.id.more_login_input_newpassword_et);
//		copypassworld = (EditText) findViewById(R.id.more_login_input_repasswords_et);
//	}
//
//	// protected class Linsener implements OnClickListener {
//	//
//	// @Override
//	// public void onClick(View v) {
//	// if (v == save) {
//	// save();
//	// }
//	// if (v == back) {
//	// finish();
//	// }
//	//
//	// }
//	//
//	// }
//
//	public void save() {
//
//		String oldpassword = oldePassworld.getText().toString().trim();
//
//		String newpassword = newpassworld.getText().toString().trim();
//		String renpassword = copypassworld.getText().toString().trim();
//
//		if (mOldLoginPassword != null) {
//			if (oldpassword.equals("")) {
//				Toast.makeText(MoreLoginPassworldManager.this, "原始密码不能为空!", 0)
//						.show();
//				return;
//			}
//			if (oldpassword != mUserInfo.pswd) {
//
//				Toast.makeText(MoreLoginPassworldManager.this, "原始密码输入不正确!", 0)
//						.show();
//				return;
//			}
//		}
//
//		if (newpassword.length() < 6) {
//			Toast.makeText(MoreLoginPassworldManager.this, "密码必须为多余6位数字!", 0)
//					.show();
//			return;
//		}
//
//		if (!newpassword.equals(renpassword)) {
//			Toast.makeText(MoreLoginPassworldManager.this, "两次密码不一致，请重新输入!", 0)
//					.show();
//			return;
//		}
//		System.out.println("userInfo.pswd:" + mUserInfo.pswd);
//		System.out.println("userInfo.name:" + mUserInfo.name);
//		System.out.println("newpassword:" + newpassword);
//		// 保存新密码
//		OBDHelper.updatePassword(mUserInfo.name, oldpassword, newpassword,
//				new OnCallbackListener() {
//
//					@Override
//					public void onSuccess(Result arg0) {
//						Toast.makeText(MoreLoginPassworldManager.this,
//								"密码设置成功!", 0).show();
//
//					}
//
//					@Override
//					public void onFailure(Result arg0) {
//						// TODO Auto-generated method stub
//
//						System.out.println("OBDHelper.updatePassword:"
//								+ "Failure");
//					}
//				});
//		GlobalApplication.getApplication().setCurrUser(null);
//
//		setResult(1);
//		this.finish();
//	}
//
//	private String getUserName() {
//
//		return mUserInfo.name;
//	}
// }
