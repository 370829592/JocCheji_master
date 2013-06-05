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

public class MoreYoujiaSetting extends AbsSubActivity {
	private EditText youjiaEditText;
	private String number;
	private SharedPreferences sharedPreferences;
	public static final String FILENAME = JocParameter.FILENAME_OIL;


	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.more_youjiasetting);
		
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		setTitle(getString(R.string.gd_yjsz));	
		init();
		check();
		sharedPreferences = super.getSharedPreferences(FILENAME,
				Activity.MODE_PRIVATE);
		if (FILENAME.length() > 0) {
			youjiaEditText.setText(sharedPreferences.getString(
					JocParameter.PAY_OIL, number));
		}
		if (FILENAME.length() == 0) {
			save();
		}
	}

	private void init() {

		showBackButton().setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				goback();
			}
		});	
		showRightButton("保存").setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				save();
			}
		});
//		back = (Button) findViewById(R.id.more_youjiasetting_back);
//		save = (Button) findViewById(R.id.more_youjianumber_save_btn);
		youjiaEditText = (EditText) findViewById(R.id.more_youjianumber_et);
//		back.setOnClickListener(new Listener());
	}


	private String save() {

		number = youjiaEditText.getText().toString().trim();
		if (number.length() > 4) {
			Toast.makeText(MoreYoujiaSetting.this, "您设定的油价有点离谱了", 1000).show();
		}
		if (number.length() == 0) {
			Toast.makeText(MoreYoujiaSetting.this, "没有输入数据，请输入", 1000).show();
		}
		if(number.length() > 0&& number.length() <= 4){
			
		

		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(JocParameter.PAY_OIL, number);
		editor.commit();
		Toast.makeText(MoreYoujiaSetting.this, "油价已设定", 1000).show();
		MoreYoujiaSetting.this.finish();}

		return number;
	}

	private void check() {
		number = youjiaEditText.getText().toString().trim();
		if (number.length() > 6) {
			Toast.makeText(MoreYoujiaSetting.this, "您设定的油价有点离谱了", 1000).show();
		}
	}

}
