package com.calinks.vehiclemachine.listener;
import com.calinks.vehiclemachine.model.FaultInfo;


/**
 * ��ѯ�����������
 * @author Administrator
 *
 */
public interface OnCheckedFaultInfoListener {
	/**
	 * ��ȡ������ʱ
	 * @param fInfo ��������Ϣ
	 */
	public void onGetInfo(FaultInfo fInfo);
}
