package com.calinks.vehiclemachine.listener;

import com.provider.model.resources.RealTimeOBD;

/**
 * ��ȡʵʱ��ݼ�����
 * @author chao
 *
 */
public interface OnGetRealDataListener {
	
	public void onGetRealTimeOBD(RealTimeOBD rtdata);

}
