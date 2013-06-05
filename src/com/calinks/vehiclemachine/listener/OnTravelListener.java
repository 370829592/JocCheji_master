package com.calinks.vehiclemachine.listener;

/**
 * 行程监听器
 * @author chao
 *
 */
public interface OnTravelListener {
	/**
	 * 当行程开始时
	 * @param time  当前时间
	 */
	public void onTravelStart(long startTime);
	
	/**
	 * 当行程结束时
	 * @param travelMeters  行程公里数
	 * @param fuelConsumption 行程耗油量
	 * @param time  行程结束时当前时间
	 */
	public void onTravelEnd(float travelMeters,float fuelConsumption,long endTime);
	
	
	
}
