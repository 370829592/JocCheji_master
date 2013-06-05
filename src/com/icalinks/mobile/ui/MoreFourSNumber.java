package com.icalinks.mobile.ui;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.icalinks.common.JocParameter;
import com.icalinks.jocyjt.R;
import com.icalinks.mobile.ui.activity.AbsSubActivity;

public class MoreFourSNumber extends
		AbsSubActivity {
	public static final String FILENAME = JocParameter.FILENAME_4S;
	private EditText numberEditText;
	private String number;
	private SharedPreferences sharedPreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.more_foursnumber);
		init();
		sharedPreferences = super.getSharedPreferences(FILENAME,
				Activity.MODE_PRIVATE);
		if (FILENAME.length() > 0) {
			numberEditText.setText(sharedPreferences.getString(
					JocParameter.FORTH_S, number));
		}
		if (FILENAME.length() == 0) {
			save();
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		setTitle(getString(R.string.gd_4sddhsz));
		init();
	}


	private void init() {
		
		
		showRightButton("保存").setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				save();
			}
		});

		showBackButton().setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				goback();
			}
		});
		// back = (Button) findViewById(R.id.more_foursnumber_back);
		// save = (Button) findViewById(R.id.more_foursnumber_save_btn);
		// back.setOnClickListener(new Listener());
		// save.setOnClickListener(new Listener());
		numberEditText = (EditText) findViewById(R.id.more_foursnumber_et);
	}


	private String save() {

		number = numberEditText.getText().toString().trim();
		if (number.length() == 0) {
			Toast.makeText(MoreFourSNumber.this, "没有输入数据，请输入", 1000).show();
		} else {
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putString(JocParameter.FORTH_S, number);
			editor.commit();
			Toast.makeText(MoreFourSNumber.this, "4S电话号码已设定", 1000).show();
			MoreFourSNumber.this.finish();
		}
		return number;
	}
}
