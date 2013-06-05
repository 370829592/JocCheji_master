package com.icalinks.mobile.ui.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.icalinks.common.MParams;
import com.icalinks.mobile.ui.fragment.DocACTJFragment;
import com.icalinks.mobile.ui.fragment.DocConsumData;
import com.icalinks.mobile.ui.fragment.InfoCatTmpsFragment;
import com.icalinks.mobile.ui.fragment.InfoInsureFragment;
import com.icalinks.mobile.ui.fragment.InfoRecordFragment;
import com.icalinks.mobile.ui.fragment.InfoSingleFragment;
import com.icalinks.mobile.ui.fragment.RmctCarinfFragment;
import com.icalinks.mobile.util.MLogUtils;

/**
 * @ClassName: 类名称:PublicActivity
 * @author 作者 E-mail: wc_zhang@calinks.com.cn
 * @Description: TODO 为Fragment创建一个新的容器
 * @version 创建时间：2013-5-24 上午10:54:46
 */
public class PublicActivity extends AbsSubActivity {

	private DocConsumData doc0;
	private DocACTJFragment doc1;
	// //单次行驶记录
	private InfoSingleFragment doc2;
	// // 消费记录
	private InfoRecordFragment doc3;
	// //胎压检测
	private InfoCatTmpsFragment doc4;

	private RmctCarinfFragment ms1;
	private InfoInsureFragment ms6;

	private int id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		id = getIntent().getExtras().getInt(MParams.FLAG_INTENT);
		LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
		View v = null;
		switch (id) {
		// 汽车医生======================================================
		case MParams.QCYS_YHSJ:
			doc0 = new DocConsumData();
			doc0.setActivity(this);
			v = doc0.onCreateView(inflater, null, savedInstanceState) ;
			break;
		case MParams.QCYS_ACTJ:
			doc1 = new DocACTJFragment();
			doc1.setActivity(this);
			v = doc1.onCreateView(inflater, null, savedInstanceState);
			break;
		case MParams.QCYS_DCXSJL:
			doc2 = new InfoSingleFragment();
			doc2.setActivity(this);
			v= doc2.onCreateView(inflater, null, savedInstanceState) ;
			break;
		case MParams.QCYS_XFJL:
			doc3 = new InfoRecordFragment();
			doc3.setActivity(this);
			v = doc3.onCreateView(inflater, null, savedInstanceState) ;
			break;
		case MParams.QCYS_TYJC:
			doc4 = new InfoCatTmpsFragment();
			doc4.setActivity(this);
			v = doc4.onCreateView(inflater, null, savedInstanceState) ;
			break;
		// 汽车秘书-==============================================
		case MParams.MS_CZB:
			// new NearActivity();
			break;
		case MParams.MS_CLXX:
			ms1 = new RmctCarinfFragment();
			ms1.setActivity(this);
			v = ms1.onCreateView(inflater, null, savedInstanceState) ;
			break;
		case MParams.MS_DLJY:
			// 打电话功能
			break;
		case MParams.MS_DJFW:
			// 打电话功能
			break;
		case MParams.MS_YJDH:
			// 打电话然后跳转地图
			break;
		case MParams.MS_XXZX:
			// MsgsActivity
			break;
		case MParams.MS_CXBX:
			ms6 = new InfoInsureFragment();
			ms6.setActivity(this);
			v = ms6.onCreateView(inflater, null, savedInstanceState) ;
			break;
		// ===============================================================
		}
		
		
		if(v!=null)setContentView(v);
		MLogUtils.printEMsg("onCreate();"+id);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		switch (id) {
		// 汽车医生======================================================
		case MParams.QCYS_YHSJ:
			doc0.onPause();
			break;
		case MParams.QCYS_ACTJ:
			doc1.onPause();
			break;
		case MParams.QCYS_DCXSJL:
			doc2.onPause();
			break;
		case MParams.QCYS_XFJL:
			doc3.onPause();
			break;
		case MParams.QCYS_TYJC:
			doc4.onPause();
			break;
		// 汽车秘书-==============================================
		case MParams.MS_CZB:
			// new NearActivity();
			break;
		case MParams.MS_CLXX:
			ms1.onPause();
			break;
		case MParams.MS_DLJY:
			// 打电话功能
			break;
		case MParams.MS_DJFW:
			// 打电话功能
			break;
		case MParams.MS_YJDH:
			// 打电话然后跳转地图
			break;
		case MParams.MS_XXZX:
			// MsgsActivity
			break;
		case MParams.MS_CXBX:
			ms6.onPause();
			break;
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		switch (id) {
		// 汽车医生======================================================
		case MParams.QCYS_YHSJ:
			doc0.onResume();
			break;
		case MParams.QCYS_ACTJ:
			doc1.onResume();
			break;
		case MParams.QCYS_DCXSJL:
			doc2.onResume();
			break;
		case MParams.QCYS_XFJL:
			doc3.onResume();
			break;
		case MParams.QCYS_TYJC:
			doc4.onResume();
			break;
		// 汽车秘书-==============================================
		case MParams.MS_CZB:
			// new NearActivity();
			break;
		case MParams.MS_CLXX:
			ms1.onResume();
			break;
		case MParams.MS_DLJY:
			// 打电话功能
			break;
		case MParams.MS_DJFW:
			// 打电话功能
			break;
		case MParams.MS_YJDH:
			// 打电话然后跳转地图
			break;
		case MParams.MS_XXZX:
			// MsgsActivity
			break;
		case MParams.MS_CXBX:
			ms6.onResume();
			break;
		}
	}
}
