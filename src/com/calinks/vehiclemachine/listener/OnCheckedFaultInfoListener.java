package com.calinks.vehiclemachine.listener;
import com.calinks.vehiclemachine.model.FaultInfo;


/**
 * 查询故障码监听器
 * @author Administrator
 *
 */
public interface OnCheckedFaultInfoListener {
	/**
	 * 获取故障码时
	 * @param fInfo 故障码信息
	 */
	public void onGetInfo(FaultInfo fInfo);
}
