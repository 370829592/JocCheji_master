package com.icalinks.mobile.ui.activity;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.ActivityGroup;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.icalinks.jocyjt.R;

/** 继承该类并实现其五个抽象方法即可 */
public abstract class AbsActivityGroup extends ActivityGroup{
	
	/** 源Intent */
	protected Intent fromIntent;
	
	/** 功能模块跳转的目标Intent */
	protected Intent targetIntent = new Intent();
	
//	/** Group的标题控件 */ 
//	protected TextView topBarTitleTextView;
//	
//	/** Group的标题内容 */ 
//	protected String topBarName = "";
	
	/** 用来加载子Activity的布局 */
	private LinearLayout container = null;
	
	/** 选项卡 */
	private RadioGroup radioGroup = null;
	
	/** 选中标签的ID */
	private int radioGroupCheckId;
	
	/** 切换标签动作的标志位 */
	private boolean changedFlag;
	
	/** 选项卡的所有标签 */
	private RadioButton[] radioButtons = null;
	
	/** 选项卡所有标签的ID */
	private int[] radioButtonIds = null;
	
	
	
	/** 标签ID对应的初始Activity集合 */
	private Map<Integer,Class<? extends Activity>> classes = new HashMap<Integer,Class<? extends Activity>>();
	
	/** 标签ID对应的当前Activity集合 */
	public Map<Integer,Class<? extends Activity>> currentClasses = new HashMap<Integer,Class<? extends Activity>>();;
	
	/**
	 * 在子类中实现的设定布局的方法，Activity的布局的id必须为activity_group_container；
	 * 选项卡的id必须为activity_group_radioGroup
	 */
	protected abstract int getLayoutResourceId();
	
	/** 在子类中需要实现的获取选项卡所有标签的ID的方法 */ 
	protected abstract int[] getRadioButtonIds();
	
	
	/** 在子类中需要实现的获取选项卡所有标签ID对应的初始Activity的方法 */ 
	public abstract Class<? extends Activity>[] getClasses();
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResourceId());         
        // 获取源Intent
		fromIntent = getIntent();
		// 设定原始数据
        setData();
        // 初始化控件
        initWidgetStatic();
    }
			
	/** 设定数据源的方法 */ 
	protected  void setData(){
		radioButtonIds = getRadioButtonIds();
		for(int i=0;i<radioButtonIds.length;i++){
			classes.put(radioButtonIds[i], getClasses()[i]);
			currentClasses.put(radioButtonIds[i], getClasses()[i]);
		}
	}
	
	/** 初始化控件 */ 
	protected void initWidgetStatic(){
		container = (LinearLayout) findViewById(R.id.home_linearLayout);
		radioGroup = (RadioGroup) findViewById(R.id.home_radio);
		radioButtons = new RadioButton[radioButtonIds.length];
		for(int i=0;i<radioButtons.length;i++){
			radioButtons[i] = (RadioButton) findViewById(radioButtonIds[i]);
			radioButtons[i].setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(!changedFlag){
						targetIntent.setClass(AbsActivityGroup.this,
								classes.get(radioGroupCheckId));
						launchActivity(targetIntent);
					}
					changedFlag = false;
				}
			});
		}

		// 给选项卡设定监听
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				currentClasses.put(radioGroupCheckId, AbsActivityGroup.this
						.getCurrentActivity().getClass());
				setTargetIntent(checkedId);	
				launchActivity(targetIntent);
				radioGroupCheckId = checkedId;
				changedFlag = true;
			}
		});	
		
		// 初始化加载
		radioGroupCheckId = getCheckedRadioButtonId();
		setTargetIntent(radioGroupCheckId);
		launchNewActivity(targetIntent);
	}
	
	/** 设定目标Intent的方法 */
	protected void setTargetIntent(int checkedId){
		targetIntent.setClass(AbsActivityGroup.this, currentClasses.get(checkedId));
	}
	
	/** ActivityGroup加载新的子Activity的方法(创建新的) */ 
	public void launchNewActivity(Intent intent) {
		container.removeAllViews();
		container.addView(getLocalActivityManager().startActivity(
				intent.getComponent().getShortClassName()+getCheckedRadioButtonId(), intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
				.getDecorView());
	}
	
	/** ActivityGroup加载新的子Activity的方法(创建新的) */ 
	public void launchNewActivityForResult(AbsSubActivity requestSubActivity, Intent intent, int requestCode) {
		intent.putExtra("requestCode", requestCode);
		container.removeAllViews();
		container.addView(getLocalActivityManager().startActivity(
				intent.getComponent().getShortClassName()+getCheckedRadioButtonId(), intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
				.getDecorView());
		((AbsSubActivity)getCurrentActivity()).setRequestSubActivity(requestSubActivity);
	}
	
	/** ActivityGroup加载子Activity的方法(先看有没有，有则加载原来的，否则创建新的) */ 
	public void launchActivity(Intent intent) {
		container.removeAllViews();
		container.addView(getLocalActivityManager().startActivity(
				intent.getComponent().getShortClassName()+getCheckedRadioButtonId(), intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP))
				.getDecorView());
	}
	
	/** 这个方法用于获取当前ActivityGroup的选项卡按下的单选按钮的ID */  
	public int getCheckedRadioButtonId(){
		return radioGroup.getCheckedRadioButtonId();
	}
	
//	/** 这个方法用于获取当前ActivityGroup的topBarName */ 
//	public String getTopBarName(){
//		return topBarName;
//	}

}
