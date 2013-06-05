package com.calinks.vehiclemachine.listener;

/**
 * �г̼�����
 * @author chao
 *
 */
public interface OnTravelListener {
	/**
	 * ���г̿�ʼʱ
	 * @param time  ��ǰʱ��
	 */
	public void onTravelStart(long startTime);
	
	/**
	 * ���г̽���ʱ
	 * @param travelMeters  �г̹�����
	 * @param fuelConsumption �г̺�����
	 * @param time  �г̽���ʱ��ǰʱ��
	 */
	public void onTravelEnd(float travelMeters,float fuelConsumption,long endTime);
	
	
	
}
